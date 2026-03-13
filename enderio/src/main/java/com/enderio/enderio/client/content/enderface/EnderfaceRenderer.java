package com.enderio.enderio.client.content.enderface;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.client.foundation.renderer.OutlineBuffer;
import com.enderio.enderio.content.enderface.EnderfaceTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModelWrapper;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class EnderfaceRenderer implements TravelRenderer<EnderfaceTravelTarget> {

    @Override
    public void render(EnderfaceTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack,
            double distanceSquared, boolean active, float partialTick) {
        poseStack.pushPose();
        poseStack.translate(travelData.pos().getX(), travelData.pos().getY(), travelData.pos().getZ());
        Minecraft minecraft = Minecraft.getInstance();
        OutlineBuffer outlineBuffer = OutlineBuffer.INSTANCE;
        int outlineColor = 0xFFFFFFFF;
        if (active) {
            outlineColor = ChatFormatting.GOLD.getColor() == null ? 0xFFFFFFFF : ARGB.opaque(ChatFormatting.GOLD.getColor());
        }

        // Render Model
        BlockState blockState = minecraft.level.getBlockState(travelData.pos());

        BlockModel blockModel = minecraft.getModelManager().getBlockModelSet().get(blockState);
        // TODO: 26.1 - renderModel is gonezo
//        BakedQuadOutput output = (pose, quad, brightness, color, lightmapCoord, overlayCoords) -> {
//            VertexConsumer buffer = outlineBuffer.getBuffer(RenderTypes.solidMovingBlock());
//            buffer.putBulkData(pose, quad, brightness, color, lightmapCoord, overlayCoords);
//        };
//
//        ModelBlockRenderer.renderModel(poseStack.last(), output, blockModel, -1, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
//            minecraft.level, travelData.pos(), blockState);

        // Render line
        float linesize;
        if (distanceSquared > 85 * 85) {
            linesize = Minecraft.getInstance().getWindow().getAppropriateLineWidth();
        } else if (distanceSquared > 38 * 38) {
            linesize = 3;
        } else {
            linesize = 5;
        }

        ShapeRenderer.renderShape(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderTypes.lines()), Shapes.block(),
            0,0,0, outlineColor, linesize);

        poseStack.popPose();
        minecraft.renderBuffers().bufferSource().endBatch();
    }
}
