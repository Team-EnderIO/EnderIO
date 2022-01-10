package com.enderio.machines.client.rendering.travel;

import com.enderio.base.client.renderer.teleportation.TeleportationRenderer;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;

public class TravelAnchorRenderer implements TeleportationRenderer<AnchorTravelTarget> {

    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack) {
         poseStack.pushPose();
         poseStack.translate(travelData.getPos().getX(), travelData.getPos().getY(), travelData.getPos().getZ());
         //TODO: Render the Travel Anchor name icon and do some other stuff, pls Crazy, be a rendering genius
         Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.AMETHYST_BLOCK.defaultBlockState(), poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), 0xF000F0,
             OverlayTexture.NO_OVERLAY);
         poseStack.popPose();
    }
}
