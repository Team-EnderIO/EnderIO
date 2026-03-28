package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.foundation.model.ModelRenderUtil;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class IOConfigSceneRenderer extends PictureInPictureRenderer<IOConfigSceneRenderState> {
    private static final Identifier SELECTED_ICON = EnderIO.id("block/overlay/selected_face");

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

        if (state.selection() != null) {
            renderSelection(poseStack, state.selection().getFirst(), state.selection().getSecond());
        }

        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, IOConfigSceneBlock block) {
        poseStack.pushPose();
        poseStack.translate(new Vec3(block.pos()));

        block.blockModelRenderState().submit(poseStack, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        featureRenderDispatcher.renderAllFeatures();
        bufferSource.endBatch();

        poseStack.popPose();
    }

    private void renderSelection(PoseStack poseStack, BlockPos pos, Direction side) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(SELECTED_ICON);

        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.blockScreenEffect(sprite.atlasLocation()));

        poseStack.pushPose();

        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

        Vector3f[] vec = ModelRenderUtil.createQuadVerts(side, 0, 1, 1);

        builder.addVertex(poseStack.last(), vec[0].x(), vec[0].y(), vec[0].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU0(), sprite.getV0());
        builder.addVertex(poseStack.last(), vec[1].x(), vec[1].y(), vec[1].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU0(), sprite.getV1());
        builder.addVertex(poseStack.last(), vec[2].x(), vec[2].y(), vec[2].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU1(), sprite.getV1());
        builder.addVertex(poseStack.last(), vec[3].x(), vec[3].y(), vec[3].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU1(), sprite.getV0());

        bufferSource.endBatch();

        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "EnderIO IO Config Overlay";
    }
}
