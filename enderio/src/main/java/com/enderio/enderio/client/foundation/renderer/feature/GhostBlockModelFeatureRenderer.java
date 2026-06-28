package com.enderio.enderio.client.foundation.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.BlockModelFeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Based on {@link BlockModelFeatureRenderer} but always rendering the block with transparency.
 */
public class GhostBlockModelFeatureRenderer extends RenderTypeFeatureRenderer<GhostBlockModelFeatureRenderer.Submit> {
    public static final FeatureRendererType<GhostBlockModelFeatureRenderer.Submit> TYPE = FeatureRendererType.create("EnderIO Ghost Block Model");
    private static final Direction[] DIRECTIONS = Direction.values();
    private final QuadInstance quadInstance = new QuadInstance();

    @Override
    protected void buildGroup(FeatureFrameContext context, List<Submit> submits) {
        for (Submit submit : submits) {
            VertexConsumer buffer = this.getVertexBuilder(Sheets.translucentBlockItemSheet());
            VertexConsumer wrappedBuffer = new AlphaVertexConsumerWrapper(submit.sheetedDecalPose() != null
                ? new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1.0F)
                : buffer);

            this.quadInstance.setLightCoords(submit.lightCoords());
            this.quadInstance.setOverlayCoords(submit.overlayCoords());

            for (BlockStateModelPart part : submit.modelParts()) {
                putPartQuads(part, submit.pose(), this.quadInstance, submit.tintColor(), submit.tintLayers(), wrappedBuffer);
            }
        }
    }

    private static void putPartQuads(
        BlockStateModelPart part, PoseStack.Pose pose, QuadInstance quadInstance, int baseTintColor, int[] tintLayers, VertexConsumer buffer
    ) {
        for (Direction direction : DIRECTIONS) {
            for (BakedQuad quad : part.getQuads(direction)) {
                putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
            }
        }

        for (BakedQuad quad : part.getQuads(null)) {
            putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
        }
    }

    private static void putQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance, int baseTintColor, int[] tintLayers, VertexConsumer buffer) {
        int tintIndex = quad.materialInfo().tintIndex();
        boolean useTintLayer = tintIndex != -1 && tintIndex < tintLayers.length;
        instance.setColor(useTintLayer ? ARGB.multiply(baseTintColor, tintLayers[tintIndex]) : baseTintColor);
        buffer.putBakedQuad(pose, quad, instance);
    }

    public record Submit(
        PoseStack.Pose pose,
        RenderType renderType,
        List<BlockStateModelPart> modelParts,
        int[] tintLayers,
        int lightCoords,
        int overlayCoords,
        int tintColor,
        PoseStack.@Nullable Pose sheetedDecalPose
    ) implements TranslucentSubmit {
        @Override
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq(this.pose.pose(), 0.5F, 0.5F, 0.5F);
        }

        @Override
        public FeatureRendererType<GhostBlockModelFeatureRenderer.Submit> featureType() {
            return GhostBlockModelFeatureRenderer.TYPE;
        }
    }
}
