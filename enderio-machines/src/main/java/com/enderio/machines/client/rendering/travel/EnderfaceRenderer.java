package com.enderio.machines.client.rendering.travel;

import com.enderio.base.api.travel.TravelRenderer;
import com.enderio.machines.common.travel.EnderfaceTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.FastColor;
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

        BakedModel blockModel = minecraft.getBlockRenderer().getBlockModel(blockState);
        VertexConsumer solid = buffer.getBuffer(RenderType.solid());
        minecraft.getBlockRenderer()
                .getModelRenderer()
                .renderModel(poseStack.last(), solid, blockState, blockModel, 1, 1, 1, 0xF000F0,
                        OverlayTexture.NO_OVERLAY);

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
        LevelRenderer.renderLineBox(poseStack, lines, 0, 0, 0, 1, 1, 1, FastColor.ARGB32.red(color) / 255F,
                FastColor.ARGB32.green(color) / 255F, FastColor.ARGB32.blue(color) / 255F, 1);

        poseStack.popPose();
        minecraft.renderBuffers().bufferSource().endBatch();
    }
}
