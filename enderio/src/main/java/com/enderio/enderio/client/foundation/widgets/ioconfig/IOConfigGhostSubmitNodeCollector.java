package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.enderio.enderio.client.foundation.renderer.feature.GhostBlockModelFeatureRenderer;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.BlockModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class IOConfigGhostSubmitNodeCollector implements SubmitNodeCollector {
    private final SubmitNodeCollection passthrough;

    public IOConfigGhostSubmitNodeCollector(SubmitNodeCollection passthrough) {
        this.passthrough = passthrough;
    }

    @Override
    public OrderedSubmitNodeCollector order(int order) {
        Preconditions.checkArgument(order == 0, "GhostSubmitNodeCollector only supports order 0");
        return this;
    }

    @Override
    public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) {
        passthrough.submitShadow(poseStack, radius, pieces);
    }

    @Override
    public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords,
        CameraRenderState camera) {
        passthrough.submitNameTag(poseStack, nameTagAttachment, offset, name, seeThrough, lightCoords, camera);
    }

    @Override
    public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode,
        int lightCoords, int color, int backgroundColor, int outlineColor) {
        passthrough.submitText(poseStack, x, y, string, dropShadow, displayMode, lightCoords, color, backgroundColor, outlineColor);
    }

    @Override
    public void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation) {
        passthrough.submitFlame(poseStack, renderState, rotation);
    }

    @Override
    public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState) {
        passthrough.submitLeash(poseStack, leashState);
    }

    // Redirects to the ghost model renderer
    @Override
    public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords,
        int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        passthrough.submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords, tintedColor, sprite, outlineColor, crumblingOverlay);
    }

    @Override
    public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState, int outlineColor) {
        passthrough.submitMovingBlock(poseStack, movingBlockRenderState, outlineColor);
    }

    // Redirects from the built-in block model renderer to the ghost block model renderer.
    @Override
    public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tintLayers, int lightCoords,
        int overlayCoords, int outlineColor) {
        PoseStack.Pose pose = poseStack.last().copy();
        if (!renderType.isOutline()) {
            GhostBlockModelFeatureRenderer.Submit submit = new GhostBlockModelFeatureRenderer.Submit(
                pose, renderType, parts, tintLayers, lightCoords, overlayCoords, -1, null
            );
            if (renderType.hasBlending()) {
                passthrough.translucentBlocksAndItems.submit(submit);
            } else {
                passthrough.solid.submit(submit);
            }
        }

        if (outlineColor != 0) {
            RenderType outlineRenderType = getOutlineRenderType(renderType);
            if (outlineRenderType != null) {
                passthrough.outline
                    .submit(
                        new BlockModelFeatureRenderer.Submit(
                            pose, outlineRenderType, parts, BlockModelRenderState.EMPTY_TINTS, 15728880, OverlayTexture.NO_OVERLAY, outlineColor, null
                        )
                    );
            }
        }
    }

    private static @Nullable RenderType getOutlineRenderType(RenderType renderType) {
        if (renderType.isOutline()) {
            return renderType;
        } else {
            return renderType.outline().isPresent() ? renderType.outline().get() : null;
        }
    }

    @Override
    public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress) {
        passthrough.submitBreakingBlockModel(poseStack, parts, progress);
    }

    @Override
    public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
        passthrough.submitShapeOutline(poseStack, shape, renderType, color, width, afterTerrain);
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers,
        List<BakedQuad> quads, ItemStackRenderState.FoilType foilType) {
        passthrough.submitItem(poseStack, displayContext, lightCoords, overlayCoords, outlineColor, tintLayers, quads, foilType);
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
        passthrough.submitCustomGeometry(poseStack, renderType, customGeometryRenderer);
    }

    @Override
    public void submitQuadParticleGroup(QuadParticleRenderState particles) {
        passthrough.submitQuadParticleGroup(particles);
    }

    @Override
    public void submitGizmoPrimitives(DrawableGizmoPrimitives.Group group, CameraRenderState camera, boolean onTop) {
        passthrough.submitGizmoPrimitives(group, camera, onTop);
    }
}
