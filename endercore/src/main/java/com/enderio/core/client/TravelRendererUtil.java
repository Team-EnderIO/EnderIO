package com.enderio.core.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

public class TravelRendererUtil {

    private TravelRendererUtil() {

    }

    public static final BackdropTintingBufferSource FEATURE_BUFFER = new BackdropTintingBufferSource();
    public static final OutlineBufferSource FEATURE_OUTLINE_BUFFER = new OutlineBufferSource();
    public static final MultiBufferSource.BufferSource TEXT_BUFFER = MultiBufferSource.immediate(new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE));

    public static final FeatureRenderDispatcher FEATURE = new FeatureRenderDispatcher(
        new SubmitNodeStorage(),
        Minecraft.getInstance().getModelManager(),
        FEATURE_BUFFER,
        Minecraft.getInstance().getAtlasManager(),
        FEATURE_OUTLINE_BUFFER,
        Minecraft.getInstance().renderBuffers().crumblingBufferSource(),
        Minecraft.getInstance().font,
        Minecraft.getInstance().gameRenderer.getGameRenderState()
    );

    public static final SubmitNodeStorage NODE = FEATURE.getSubmitNodeStorage();

    public static void renderFeatures() {
        FEATURE.renderAllFeatures();
        FEATURE_BUFFER.endBatch();
        FEATURE_OUTLINE_BUFFER.endOutlineBatch();
    }

    public static void renderBackdrop(PoseStack poseStack, int packedLight, int color, Lazy<BlockModel> model) {
        BlockModelRenderState renderState = new BlockModelRenderState();
        model.get().update(renderState, Blocks.AIR.defaultBlockState(), BlockDisplayContext.create(), 42);

        FEATURE_BUFFER.setTintColor(color);
        try {
            renderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, 0);
            renderFeatures();
        } finally {
            FEATURE_BUFFER.clearTintColor();
        }
    }

    public static void renderBlockModel(PoseStack poseStack, BlockState blockState, int packedLight) {
        BlockModelRenderState modelRenderState = new BlockModelRenderState();
        Minecraft.getInstance().getBlockModelResolver().update(modelRenderState, blockState, BlockDisplayContext.create());

        modelRenderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, 0);
        renderFeatures();
    }

    public static class BackdropTintingBufferSource extends MultiBufferSource.BufferSource {

        private int tintColor = -1;

        private BackdropTintingBufferSource() {
            super(new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE), Object2ObjectSortedMaps.emptyMap());
        }

        public void setTintColor(int tintColor) {
            this.tintColor = tintColor;
        }

        public void clearTintColor() {
            this.tintColor = -1;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            if (this.tintColor != -1) {
                // Tinting is only used for the standalone backdrop model, which must stay translucent.
                renderType = Sheets.translucentBlockItemSheet();
            }

            VertexConsumer buffer = super.getBuffer(renderType);
            return this.tintColor == -1 ? buffer : new TintingVertexConsumer(buffer, this.tintColor);
        }
    }

    public record TintingVertexConsumer(VertexConsumer delegate, int tintColor) implements VertexConsumer {

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.delegate.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            this.delegate.setColor(ARGB.multiply(ARGB.color(a, r, g, b), this.tintColor));
            return this;
        }

        @Override
        public VertexConsumer setColor(int color) {
            this.delegate.setColor(ARGB.multiply(color, this.tintColor));
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.delegate.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            this.delegate.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            this.delegate.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            this.delegate.setNormal(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            this.delegate.setLineWidth(width);
            return this;
        }
    }
}
