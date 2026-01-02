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
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class EnderfaceRenderer implements TravelRenderer<EnderfaceTravelTarget> {

    @Override
    public void render(EnderfaceTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack,
            double distanceSquared, boolean active, float partialTick) {
        poseStack.pushPose();
        poseStack.translate(travelData.pos().getX(), travelData.pos().getY(), travelData.pos().getZ());
        Minecraft minecraft = Minecraft.getInstance();
        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        int color = 0xFFFFFFFF;
        if (active) {
            color = ChatFormatting.GOLD.getColor() == null ? 0xFFFFFFFF : ARGB.opaque(ChatFormatting.GOLD.getColor());
        }

        // Render Model
        BlockState blockState = minecraft.level.getBlockState(travelData.pos());

        BlockStateModel blockModel = minecraft.getBlockRenderer().getBlockModel(blockState);
        VertexConsumer solid = buffer.getBuffer(RenderTypes.solidMovingBlock());

        ModelBlockRenderer.renderModel(poseStack.last(), type -> solid, blockModel, 1, 1, 1, 0xF000F0,
            OverlayTexture.NO_OVERLAY, minecraft.level, travelData.pos(), blockState);

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
            0,0,0, color, linesize);

        poseStack.popPose();
        minecraft.renderBuffers().bufferSource().endBatch();
    }
}
