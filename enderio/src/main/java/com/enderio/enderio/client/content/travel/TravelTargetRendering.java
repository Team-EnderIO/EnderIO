package com.enderio.enderio.client.content.travel;

import com.enderio.core.client.AlwaysOnTopSubmitNodeCollector;
import com.enderio.core.client.TravelRendererUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.travel.RegisterTravelRenderersEvent;
import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.api.travel.TravelTargetType;
import com.enderio.enderio.client.content.keymaps.KeymapHandler;
import com.enderio.enderio.content.travel.TravelHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT)
public class TravelTargetRendering {

    public static final ContextKey<List<ExtractTravelTarget>> DATA_KEY = new ContextKey<>(EnderIO.id("traveltargets"));
    private static Map<TravelTargetType<?>, TravelRenderer<?>> RENDERERS;

    public static void init() {
        var event = new RegisterTravelRenderersEvent();
        ModLoader.postEvent(event);
        var factories = event.getRenderers();

        RENDERERS = new HashMap<>();
        factories.forEach((t, f) -> RENDERERS.put(t, f.createRenderer()));
    }

    public static <T extends TravelTarget> TravelRenderer<T> getRenderer(TravelTargetType<T> type) {
        // noinspection unchecked
        return (TravelRenderer<T>) RENDERERS.get(type);
    }

    public static <T extends TravelTarget> void submit(T target, PoseStack poseStack, AlwaysOnTopSubmitNodeCollector collector,
            double distanceSquared, boolean isActive, float partialTick) {
        // noinspection unchecked
        getRenderer((TravelTargetType<T>) target.type()).submit(target, poseStack, collector, distanceSquared,
                isActive, partialTick);
    }

    //TODO make TravelTarget properly immutable
    public record ExtractTravelTarget(float partialTick, TravelTarget target, double distanceSquared, boolean active) {}

    @SubscribeEvent
    public static void extractLevel(ExtractLevelRenderStateEvent event) {
        List<ExtractTravelTarget> renderStates = new ArrayList<>();
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (level == null || player == null) {
            return;
        }

        if (!TravelHandler.canTeleport(player) && !KeymapHandler.showTravelTargets()) {
            return;
        }
        boolean itemTeleport = TravelHandler.canItemTeleport(player) || KeymapHandler.showTravelTargets();
        @Nullable
        TravelTarget activeTarget = TravelHandler.getTeleportAnchorTarget(player).orElse(null);
        for (TravelTarget target : TravelTargetApi.INSTANCE.getAll(level)) {
            double range = itemTeleport ? target.item2BlockRange() : target.block2BlockRange();
            double distanceSquared = target.pos().distToCenterSqr(player.position());
            if (range * range < distanceSquared || distanceSquared < TravelHandler.MIN_TELEPORTATION_DISTANCE_SQUARED
                || TravelHandler.isTeleportPositionClear(level, target.pos()).isEmpty()) {
                continue;
            }

            boolean active = activeTarget == target;

            renderStates.add(new ExtractTravelTarget(event.getDeltaTracker().getGameTimeDeltaPartialTick(true), target, distanceSquared, active));
        }
        if (!renderStates.isEmpty()) {
            event.getRenderState().setRenderData(DATA_KEY, renderStates);
        }
    }

    @SubscribeEvent
    public static void submitTargets(SubmitCustomGeometryEvent event) {
        List<ExtractTravelTarget> renderStates = event.getLevelRenderState().getRenderData(DATA_KEY);
        if (renderStates == null) {
            return;
        }

        SubmitNodeCollector submitNodeCollector = event.getSubmitNodeCollector();
        PoseStack poseStack = event.getPoseStack();
        Vec3 projectedView = event.getLevelRenderState().cameraRenderState.pos;

        // Furthest first, anything rendered later draws over the earlier targets
        renderStates.sort(Comparator.comparingDouble(ExtractTravelTarget::distanceSquared).reversed());
        int order = 1;
        for (ExtractTravelTarget state : renderStates) {
            poseStack.pushPose();
            poseStack.translate(
                state.target().pos().getX() - projectedView.x,
                state.target().pos().getY() - projectedView.y,
                state.target().pos().getZ() - projectedView.z);

            submit(state.target(), poseStack, new AlwaysOnTopSubmitNodeCollector(submitNodeCollector, order), state.distanceSquared(), state.active(), state.partialTick());
            poseStack.popPose();
            order += TravelRendererUtil.ORDERS_PER_TARGET;
        }
    }
}
