package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.travel.RegisterTravelRenderersEvent;
import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT)
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
    public static void renderLevel(RenderLevelStageEvent.AfterTripwireBlocks event) {
        // TODO: 1.21.11: Work out how we're supposed to add to the level render state.
//        ClientLevel level = Minecraft.getInstance().level;
//        LocalPlayer player = Minecraft.getInstance().player;
//        if (level == null || player == null) {
//            return;
//        }
//
//        if (!TravelHandler.canTeleport(player)) {
//            return;
//        }
//
//        boolean itemTeleport = TravelHandler.canItemTeleport(player);
//
//        @Nullable
//        TravelTarget activeTarget = TravelHandler.getTeleportAnchorTarget(player).orElse(null);
//        for (TravelTarget target : TravelTargetApi.INSTANCE.getAll(level)) {
//            double range = itemTeleport ? target.item2BlockRange() : target.block2BlockRange();
//            double distanceSquared = target.pos().distToCenterSqr(player.position());
//            if (range * range < distanceSquared || distanceSquared < TravelHandler.MIN_TELEPORTATION_DISTANCE_SQUARED
//                    || TravelHandler.isTeleportPositionClear(level, target.pos()).isEmpty()) {
//                continue;
//            }
//
//            PoseStack poseStack = event.getPoseStack();
//            poseStack.pushPose();
//            Camera mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
//            Vec3 projectedView = mainCamera.position();
//            poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
//
//            boolean active = activeTarget == target;
//
//            // needed for smooth rendering
//            // the boolean value controls whether it's still smooth while the game world is
//            // paused (e.g. /tick freeze)
//            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
//            render(target, event.getLevelRenderer(), poseStack, distanceSquared, active, partialTick);
//            poseStack.popPose();
//        }
    }
}
