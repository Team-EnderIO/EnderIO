package com.enderio.enderio.client.foundation.widgets.ioconfig;

// 26.2-port: MultiBufferSource was removed in 26.2; IOConfigSceneRenderer is stubbed until the
// 26.2 submit-node-based render pipeline is reimplemented.

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.EIOPipelineModifiers;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

public class IOConfigSceneRenderer extends PictureInPictureRenderer<IOConfigSceneRenderState> {
    private static final Identifier SELECTED_ICON = EnderIO.id("block/overlay/selected_face");

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2f;
    }

    @Override
    public Class<IOConfigSceneRenderState> getRenderStateClass() {
        return IOConfigSceneRenderState.class;
    }

    @Override
    protected void renderToTexture(IOConfigSceneRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        poseStack.pushPose();

        poseStack.mulPose(renderState.viewMatrix());

        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);

        // Render neighbors transparently, if any to show
        if (!renderState.neighborBlocks().isEmpty()) {
            RenderSystem.pushPipelineModifier(EIOPipelineModifiers.FORCE_TRANSLUCENT);

            // TODO: Ew... maybe doing this fully manually would be a better idea
            var transparentSubmitNodeCollector = new IOConfigGhostSubmitNodeCollector((SubmitNodeCollection)submitNodeCollector.order(0));

            for (var block : renderState.neighborBlocks()) {
                renderBlock(poseStack, transparentSubmitNodeCollector, block);
            }

            RenderSystem.popPipelineModifier();
        }

        // Now render the primary blocks
        for (var block : renderState.primaryBlocks()) {
            renderBlock(poseStack, submitNodeCollector, block);
        }

        if (renderState.selection() != null) {
            renderSelection(poseStack, renderState.selection().getFirst(), renderState.selection().getSecond());
        }

        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, IOConfigSceneBlock block) {
        poseStack.pushPose();
        poseStack.translate(new Vec3(block.pos()));

        block.blockModelRenderState().submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    private void renderSelection(PoseStack poseStack, BlockPos pos, Direction side) {
//        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(SELECTED_ICON);
//
//        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.blockScreenEffect(sprite.atlasLocation()));
//
//        poseStack.pushPose();
//
//        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
//
//        Vector3f[] vec = ModelRenderUtil.createQuadVerts(side, 0, 1, 1);
//
//        builder.addVertex(poseStack.last(), vec[0].x(), vec[0].y(), vec[0].z())
//            .setColor(1F, 1F, 1F, 1F)
//            .setUv(sprite.getU0(), sprite.getV0());
//        builder.addVertex(poseStack.last(), vec[1].x(), vec[1].y(), vec[1].z())
//            .setColor(1F, 1F, 1F, 1F)
//            .setUv(sprite.getU0(), sprite.getV1());
//        builder.addVertex(poseStack.last(), vec[2].x(), vec[2].y(), vec[2].z())
//            .setColor(1F, 1F, 1F, 1F)
//            .setUv(sprite.getU1(), sprite.getV1());
//        builder.addVertex(poseStack.last(), vec[3].x(), vec[3].y(), vec[3].z())
//            .setColor(1F, 1F, 1F, 1F)
//            .setUv(sprite.getU1(), sprite.getV0());
//
//        bufferSource.endBatch();
//
//        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "EnderIO IO Config Overlay";
    }
}
