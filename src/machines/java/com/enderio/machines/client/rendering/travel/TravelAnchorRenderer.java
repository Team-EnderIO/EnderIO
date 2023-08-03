package com.enderio.machines.client.rendering.travel;

import com.enderio.api.travel.TravelRenderer;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

public class TravelAnchorRenderer implements TravelRenderer<AnchorTravelTarget> {
    public static final RenderType BOLD_LINES = OutlineRenderType.createLines("bold_lines", 3);
    public static final RenderType VERY_BOLD_LINES = OutlineRenderType.createLines("very_bold_lines", 5);

    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack, double distanceSquared) {
        if (!travelData.getVisibility())
            return;
        poseStack.pushPose();
        poseStack.translate(travelData.getPos().getX(), travelData.getPos().getY(), travelData.getPos().getZ());
        Minecraft mc = Minecraft.getInstance();
        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        BlockState state = mc.level.getBlockState(travelData.getPos());
        BakedModel model = mc.getBlockRenderer().getBlockModel(state);
        VertexConsumer vertex = buffer.getBuffer(RenderType.solid());
        mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vertex, state, model, 1, 1, 1, 0xF000F0, OverlayTexture.NO_OVERLAY);
        RenderType type;
        if (distanceSquared > 85 * 85) {
            type = RenderType.lines();
        } else if (distanceSquared > 38 * 38) {
            type = BOLD_LINES;
        } else {
            type = VERY_BOLD_LINES;
        }
        VertexConsumer vert = buffer.getBuffer(type);
        LevelRenderer.renderLineBox(poseStack, vert, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1);
        poseStack.popPose();
        mc.renderBuffers().bufferSource().endBatch();

    }
}
