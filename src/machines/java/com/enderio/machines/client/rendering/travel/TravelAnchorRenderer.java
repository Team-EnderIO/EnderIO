package com.enderio.machines.client.rendering.travel;

import com.enderio.api.travel.TravelRenderer;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

public class TravelAnchorRenderer implements TravelRenderer<AnchorTravelTarget> {
    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack) {
        if (!travelData.getVisibility())
            return;
        poseStack.pushPose();
        poseStack.translate(travelData.getPos().getX(), travelData.getPos().getY(), travelData.getPos().getZ());
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource bs = mc.renderBuffers().bufferSource();
        BlockState state = mc.level.getBlockState(travelData.getPos());
        BakedModel model = mc.getBlockRenderer().getBlockModel(state);
        VertexConsumer vertex = bs.getBuffer(RenderType.solid());
        mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vertex, state, model, 1, 1, 1, 0xF000F0, OverlayTexture.NO_OVERLAY);
        //        mc.getBlockRenderer().renderSingleBlock(state, poseStack, bs, 0xF000F0, OverlayTexture.NO_OVERLAY);
        bs.endBatch();
        poseStack.popPose();
    }
}
