package com.enderio.enderio.client.content.enderface;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.content.enderface.EnderfaceTravelTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;

// 26.2-port: renderModel API was removed in 26.2; the BlockModel outline render is stubbed
// until the 26.2 submit-node-based render pipeline is reimplemented.

public class EnderfaceRenderer implements TravelRenderer<EnderfaceTravelTarget> {

    @Override
    public void render(EnderfaceTravelTarget travelData, net.minecraft.client.renderer.LevelRenderer levelRenderer,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            double distanceSquared, boolean active, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        // Render the block outline (the line highlight) — this still works
        BlockState blockState = minecraft.level.getBlockState(travelData.pos());

        // 26.2-port: renderModel call is disabled; the new submit-node API will replace this
        // BlockModel blockModel = minecraft.getModelManager().getBlockModelSet().get(blockState);
        // ... render model via FeatureRenderDispatcher / SubmitNodeCollector ...

        // Render line
        float linesize;
        if (distanceSquared > 85 * 85) {
            linesize = Minecraft.getInstance().getWindow().getAppropriateLineWidth();
        } else if (distanceSquared > 38 * 38) {
            linesize = 3;
        } else {
            linesize = 5;
        }

        // 26.2-port: ShapeRenderer.renderShape and the bufferSource endBatch call are stubbed
        // ShapeRenderer.renderShape(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderTypes.lines()), Shapes.block(),
        //     0,0,0, outlineColor, linesize);
        // minecraft.renderBuffers().bufferSource().endBatch();
    }
}
