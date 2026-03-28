package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

public class IOConfigSceneRenderer extends PictureInPictureRenderer<IOConfigSceneRenderState> {
    private final FeatureRenderDispatcher featureRenderDispatcher;
    private final SubmitNodeCollector collector;

    public IOConfigSceneRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        this.featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        this.collector = featureRenderDispatcher.getSubmitNodeStorage();
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2f;
    }

    @Override
    public Class<IOConfigSceneRenderState> getRenderStateClass() {
        return IOConfigSceneRenderState.class;
    }

    @Override
    protected void renderToTexture(IOConfigSceneRenderState state, PoseStack poseStack) {
        poseStack.pushPose();

        poseStack.mulPose(state.viewMatrix());

        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.LEVEL);

        // TODO: Not currently rendering correctly, blocks all overlap weirdly.
        if (state.shouldRenderNeighbors()) {
            for (var block : state.neighborBlocks()) {
                // TODO: Render translucent...
                renderBlock(poseStack,
                    block);
            }
        }

        for (var block : state.primaryBlocks()) {
            renderBlock(poseStack,
                block);
        }

        featureRenderDispatcher.renderAllFeatures();
        bufferSource.endBatch();

        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, IOConfigSceneBlock block) {
        poseStack.pushPose();
        poseStack.translate(new Vec3(block.pos()));

        block.blockModelRenderState().submit(poseStack, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "EnderIO IO Config Overlay";
    }
}
