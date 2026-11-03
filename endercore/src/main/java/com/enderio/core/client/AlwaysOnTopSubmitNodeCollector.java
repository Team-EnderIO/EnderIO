package com.enderio.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.model.geometry.ItemQuads;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.submit.RenderPhaseKeys;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AlwaysOnTopSubmitNodeCollector implements SubmitNodeCollector {

    private static final int NO_TINT = -1;

    private final SubmitNodeCollector passthrough;
    private final int baseOrder;
    private final int backdropTint;

    public AlwaysOnTopSubmitNodeCollector(SubmitNodeCollector passthrough, int baseOrder) {
        this(passthrough, baseOrder, NO_TINT);
    }

    private AlwaysOnTopSubmitNodeCollector(SubmitNodeCollector passthrough, int baseOrder, int backdropTint) {
        this.passthrough = passthrough;
        this.baseOrder = baseOrder;
        this.backdropTint = backdropTint;
    }

    public AlwaysOnTopSubmitNodeCollector withBackdropTint(int color) {
        return new AlwaysOnTopSubmitNodeCollector(passthrough, baseOrder, color);
    }

    private void submitOnTop(SubmitNode submit) {
        passthrough.order(baseOrder).submitSpecial(RenderPhaseKeys.ALWAYS_ON_TOP, submit);
    }

    @Override
    public AlwaysOnTopSubmitNodeCollector order(int order) {
        return new AlwaysOnTopSubmitNodeCollector(passthrough, baseOrder + order, backdropTint);
    }

    @Override
    public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode,
        int lightCoords, int color, int backgroundColor, int outlineColor) {
        submitOnTop(new TextFeatureRenderer.Submit(new Matrix4f(poseStack.last().pose()), displayMode, lightCoords,
            new TextFeatureRenderer.Content.Text(x, y, string, dropShadow, color, backgroundColor, outlineColor)));
    }

    @Override
    public void submitTextBackground(PoseStack poseStack, float x0, float y0, float x1, float y1, int color, Font.DisplayMode displayMode, int lightCoords) {
        submitOnTop(new TextFeatureRenderer.Submit(new Matrix4f(poseStack.last().pose()), displayMode, lightCoords,
            new TextFeatureRenderer.Content.StandaloneBackground(x0, y0, x1, y1, color)));
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords,
        int tintedColor, @Nullable UvMapping uvMapping, int outlineColor) {
        submitOnTop(
            new ModelFeatureRenderer.Submit<>(renderType, poseStack.last().copy(), model, state, lightCoords, overlayCoords, tintedColor, uvMapping, null));
    }

    @Override
    public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tintLayers, int lightCoords,
        int overlayCoords, int outlineColor) {
        // Tinting is only used for the standalone backdrop model, which must stay translucent.
        RenderType type = backdropTint != NO_TINT ? Sheets.translucentBlockItemSheet() : renderType;
        submitOnTop(new BlockModelFeatureRenderer.Submit(poseStack.last().copy(), type, parts, tintLayers, lightCoords, overlayCoords, backdropTint, null));
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers,
        ItemQuads quads, ItemStackRenderState.FoilType foilType) {
        submitOnTop(new ItemFeatureRenderer.Submit(poseStack.last().copy(), displayContext, lightCoords, overlayCoords, 0, tintLayers, quads.all(), foilType));
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer customGeometryRenderer) {
        submitOnTop(new CustomFeatureRenderer.Submit(poseStack.last().copy(), renderType, customGeometryRenderer));
    }

    @Override
    public void submitMultiLayerBlockModel(PoseStack poseStack, List<BlockStateModelPart> modelParts, boolean translucent, int[] tintLayers, int lightCoords,
        int overlayCoords, int outlineColor) {
        passthrough.submitMultiLayerBlockModel(poseStack, modelParts, translucent, tintLayers, lightCoords, overlayCoords, outlineColor);
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
    public void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation) {
        passthrough.submitFlame(poseStack, renderState, rotation);
    }

    @Override
    public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState) {
        passthrough.submitLeash(poseStack, leashState);
    }

    @Override
    public <S> void submitCrumblingOverlay(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords,
        int tintedColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        passthrough.submitCrumblingOverlay(model, state, poseStack, renderType, lightCoords, overlayCoords, tintedColor, crumblingOverlay);
    }

    @Override
    public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState, int outlineColor) {
        passthrough.submitMovingBlock(poseStack, movingBlockRenderState, outlineColor);
    }

    @Override
    public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress, boolean isBlockTranslucent) {
        passthrough.submitBreakingBlockModel(poseStack, parts, progress, isBlockTranslucent);
    }

    @Override
    public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
        passthrough.submitShapeOutline(poseStack, shape, renderType, color, width, afterTerrain);
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
