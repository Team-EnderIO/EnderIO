package com.enderio.enderio.client.content.enderface;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.client.foundation.renderer.OutlineBuffer;
import com.enderio.enderio.client.foundation.renderer.OutlineRenderType;
import com.enderio.enderio.content.enderface.EnderfaceTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;

public class EnderfaceRenderer implements TravelRenderer<EnderfaceTravelTarget> {
    public static final RenderType BOLD_LINES = OutlineRenderType.createLines("bold_lines", 3);
    public static final RenderType VERY_BOLD_LINES = OutlineRenderType.createLines("very_bold_lines", 5);

    @Override
    public void render(EnderfaceTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack,
            double distanceSquared, boolean active, float partialTick) {
        poseStack.pushPose();
        poseStack.translate(travelData.pos().getX(), travelData.pos().getY(), travelData.pos().getZ());
        Minecraft minecraft = Minecraft.getInstance();
        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        int color = 0xFFFFFF;
        if (active) {
            color = ChatFormatting.GOLD.getColor() == null ? 0xFFFFFF : ChatFormatting.GOLD.getColor();
        }

        // Render Model
        BlockState blockState = minecraft.level.getBlockState(travelData.pos());

        BlockStateModel blockModel = minecraft.getBlockRenderer().getBlockModel(blockState);
        VertexConsumer solid = buffer.getBuffer(RenderType.solid());

        ModelBlockRenderer.renderModel(poseStack.last(), type -> solid, blockModel, 1, 1, 1, 0xF000F0,
            OverlayTexture.NO_OVERLAY, minecraft.level, travelData.pos(), blockState);

        // Render line
        RenderType lineType;
        if (distanceSquared > 85 * 85) {
            lineType = RenderType.lines();
        } else if (distanceSquared > 38 * 38) {
            lineType = BOLD_LINES;
        } else {
            lineType = VERY_BOLD_LINES;
        }
        VertexConsumer lines = buffer.getBuffer(lineType);
        ShapeRenderer.renderLineBox(poseStack, lines, 0, 0, 0, 1, 1, 1, ARGB.red(color) / 255F,
            ARGB.green(color) / 255F, ARGB.blue(color) / 255F, 1);

        poseStack.popPose();
        minecraft.renderBuffers().bufferSource().endBatch();
    }
}
