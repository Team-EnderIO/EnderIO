package com.enderio.base.client.renderer.teleportation;

import com.enderio.api.travel.ITravelTarget;
import com.enderio.api.travel.TravelRegistry;
import com.enderio.base.common.travel.TravelSavedData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderTeleportTargets {

    @SubscribeEvent
    // TODO : DEPRECATED- REPLACE WITH RenderLevelStageEvent
    public static void renderLevel(RenderLevelLastEvent event) {
        TravelSavedData data = TravelSavedData.getTravelData(Minecraft.getInstance().level);
        for (ITravelTarget target : data.getTravelTargets()) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            Camera mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 projectedView = mainCamera.getPosition();
            poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

            //TODO: Scale, View Bobbing, (FOV/Creative Flight offset stuff)
            // pls Crazy, be a rendering genius
            TravelRegistry.getRenderer(target).render(target, event.getLevelRenderer(), poseStack);
            poseStack.popPose();
        }
    }
}
