package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.EIOPipelineModifiers;
import com.enderio.enderio.client.foundation.model.ModelRenderUtil;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

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

        // Reset the normals so the camera rotation doesn't affect lighting, otherwise most faces render dark.
        poseStack.last().normal().identity();
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.LEVEL);

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
            renderSelection(poseStack, submitNodeCollector, renderState.selection().getFirst(), renderState.selection().getSecond());
        }

        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, IOConfigSceneBlock block) {
        poseStack.pushPose();
        poseStack.translate(new Vec3(block.pos()));

        block.blockModelRenderState().submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    private void renderSelection(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, BlockPos pos, Direction side) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(SELECTED_ICON);
        RenderType renderType = RenderTypes.entityTranslucentEmissive(TextureAtlas.LOCATION_BLOCKS);

        poseStack.pushPose();
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

        // Slightly above the face to avoid z-fighting with the block.
        Vector3f[] vec = ModelRenderUtil.createQuadVerts(side, 0, 1, 1.001f);
        Vector3fc normal = side.getUnitVec3f();

        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            float[] us = { sprite.getU0(), sprite.getU0(), sprite.getU1(), sprite.getU1() };
            float[] vs = { sprite.getV0(), sprite.getV1(), sprite.getV1(), sprite.getV0() };
            for (int i = 0; i < 4; i++) {
                buffer.addVertex(pose, vec[i].x(), vec[i].y(), vec[i].z())
                    .setColor(255, 255, 255, 255)
                    .setUv(us[i], vs[i])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightCoordsUtil.FULL_BRIGHT)
                    .setNormal(pose, normal);
            }
        });

        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "EnderIO IO Config Overlay";
    }
}
