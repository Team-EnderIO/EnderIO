package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.travel.RegisterTravelRenderersEvent;
import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.api.travel.TravelTargetType;
import com.enderio.enderio.content.travel.TravelHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class TravelTargetRendering {

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

    private static <T extends TravelTarget> void render(T target, LevelRenderer levelRender, PoseStack poseStack,
            double distanceSquared, boolean isActive, float partialTick) {
        // noinspection unchecked
        getRenderer((TravelTargetType<T>) target.type()).render(target, levelRender, poseStack, distanceSquared,
                isActive, partialTick);
    }

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (level == null || player == null || event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            return;
        }

        if (!TravelHandler.canTeleport(player)) {
            return;
        }

        boolean itemTeleport = TravelHandler.canItemTeleport(player);

        @Nullable
        TravelTarget activeTarget = TravelHandler.getTeleportAnchorTarget(player).orElse(null);
        for (TravelTarget target : TravelTargetApi.INSTANCE.getAll(level)) {
            double range = itemTeleport ? target.item2BlockRange() : target.block2BlockRange();
            double distanceSquared = target.pos().distToCenterSqr(player.position());
            if (range * range < distanceSquared || distanceSquared < TravelHandler.MIN_TELEPORTATION_DISTANCE_SQUARED
                    || TravelHandler.isTeleportPositionClear(level, target.pos()).isEmpty()) {
                continue;
            }

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            Camera mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 projectedView = mainCamera.getPosition();
            poseStack.translate(
                target.pos().getX() - projectedView.x,
                target.pos().getY() - projectedView.y,
                target.pos().getZ() - projectedView.z);

            boolean active = activeTarget == target;

            // needed for smooth rendering
            // the boolean value controls whether it's still smooth while the game world is
            // paused (e.g. /tick freeze)
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);

            render(target, event.getLevelRenderer(), poseStack, distanceSquared, active, partialTick);
            poseStack.popPose();
        }
    }
}
