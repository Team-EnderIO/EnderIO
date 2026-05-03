package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.client.EnderIOClient;
import com.enderio.enderio.content.travel.travel_anchor.AnchorTravelTarget;
import com.enderio.enderio.content.travel.travel_anchor.PaintedTravelAnchorBlockEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Optional;

public class TravelAnchorRenderer implements TravelRenderer<AnchorTravelTarget> {

    //TODO, this doesn't survive a resource reload change, look to update the cache
    private static final Lazy<BlockModel> BACKDROP = Lazy.of(() -> {
        Minecraft minecraft = Minecraft.getInstance();

        return minecraft.getModelManager().getStandaloneModel(EnderIOClient.TRAVEL_ANCHOR_BACKDROP);
    });

    private static final BackdropTintingBufferSource FEATURE_BUFFER = new BackdropTintingBufferSource();
    private static final OutlineBufferSource FEATURE_OUTLINE_BUFFER = new OutlineBufferSource();
    private static final MultiBufferSource.BufferSource TEXT_BUFFER = MultiBufferSource.immediate(new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE));

    public static FeatureRenderDispatcher FEATURE = new FeatureRenderDispatcher(
        new SubmitNodeStorage(),
        Minecraft.getInstance().getModelManager(),
        FEATURE_BUFFER,
        Minecraft.getInstance().getAtlasManager(),
        FEATURE_OUTLINE_BUFFER,
        Minecraft.getInstance().renderBuffers().crumblingBufferSource(),
        Minecraft.getInstance().font,
        Minecraft.getInstance().gameRenderer.getGameRenderState()
    );

    public static SubmitNodeStorage NODE = FEATURE.getSubmitNodeStorage();

    private static void renderFeatures() {
        FEATURE.renderAllFeatures();
        FEATURE_BUFFER.endBatch();
        FEATURE_OUTLINE_BUFFER.endOutlineBatch();
    }

    private static void renderBackdrop(PoseStack poseStack, int packedLight, int color) {
        BlockModelRenderState renderState = new BlockModelRenderState();
        BACKDROP.get().update(renderState, Blocks.AIR.defaultBlockState(), BlockDisplayContext.create(), 42);

        FEATURE_BUFFER.setTintColor(color);
        try {
            renderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, 0);
            renderFeatures();
        } finally {
            FEATURE_BUFFER.clearTintColor();
        }
    }

    private static void renderBlockModel(PoseStack poseStack, BlockState blockState, int packedLight) {
        BlockModelRenderState modelRenderState = new BlockModelRenderState();
        Minecraft.getInstance().getBlockModelResolver().update(modelRenderState, blockState, BlockDisplayContext.create());

        modelRenderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, 0);
        renderFeatures();
    }

    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack,
        double distanceSquared, boolean active, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (Objects.isNull(minecraft.level) || !travelData.isVisible()) {
            return;
        }

        poseStack.pushPose();

        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.position();
        Vec3 offsetToCamera = travelData.pos().getCenter().vectorTo(cameraPos);
        Vec3 towardsCamera = offsetToCamera.normalize();
        double distanceToCamera = offsetToCamera.length();

        double anchorScale = (float) Math.sqrt(distanceToCamera);
        if (active) {
            anchorScale = anchorScale * 1.3;
        }
        anchorScale = anchorScale * (Math.sin(Math.toRadians(minecraft.options.fov().get() / 4d)));
        anchorScale = Math.max(anchorScale, 1.0F);

        {
            poseStack.translate(0.5, 0.5, 0.5);
            float s = (float) anchorScale;
            poseStack.scale(s, s, s);
            poseStack.translate(-0.5, -0.5, -0.5);
        }

        int outlineColor = 0xFF0B4D42;
        int textColor = 0xFFFFFFFF;
        if (active) {
            textColor = ChatFormatting.AQUA.getColor() == null ? 0xFFFFFFFF : ARGB.opaque(ChatFormatting.AQUA.getColor());
            outlineColor = textColor;
        }
        float outlineR = ((outlineColor & (0xFF << 16)) >> 16) / 255F;
        float outlineG = ((outlineColor & (0xFF << 8)) >> 8) / 255F;
        float outlineB = (outlineColor & 0xFF) / 255F;

        int packedLight = LightCoordsUtil.pack(15, 15);

        boolean hasIcon = travelData.icon() != Items.AIR;

        if (!hasIcon) {
            BlockState blockState = minecraft.level.getBlockState(travelData.pos());
            if (minecraft.level.getBlockEntity(travelData.pos()) instanceof PaintedTravelAnchorBlockEntity paintedTravelAnchorBlock) {
                Optional<Block> paint = paintedTravelAnchorBlock.getPrimaryPaint();

                if (paint.isPresent()) {
                    blockState = paint.get().defaultBlockState();
                }
            }

            // Render outline block
            {
                poseStack.pushPose();

                float outlineSize = active ? 0.2F : 0.15F;
                float scale = 1F + 2 * outlineSize;
                Vec3 offset = towardsCamera.scale(-scale).subtract(outlineSize, outlineSize, outlineSize);

                poseStack.translate(offset.x, offset.y, offset.z);
                poseStack.scale(scale, scale, scale);

                renderBackdrop(poseStack, packedLight, ARGB.colorFromFloat(1, outlineR, outlineG, outlineB));

                poseStack.popPose();
            }

            // Render Model
            renderBlockModel(poseStack, blockState, packedLight);

        } else {
            // Render Icon
            poseStack.pushPose();

            {
                Vector3f upDir = new Vec3(0, 1, 0)
                    .xRot(-camera.xRot() * ((float) Math.PI / 180F))
                    .yRot(-camera.yRot() * ((float) Math.PI / 180F))
                    .toVector3f();
                Vector3f direction = towardsCamera.toVector3f();
                Quaternionf iconRotation = new Quaternionf().lookAlong(direction.x(), direction.y(), direction.z(), upDir.x(), upDir.y(), upDir.z());

                Vec3 offset = towardsCamera.scale(0.9);

                poseStack.translate(offset.x() + 0.5, offset.y() + 0.5, offset.z() + 0.5);
                poseStack.mulPose(iconRotation.invert());
                float s = active ? 1.3F : 1.0F;
                poseStack.scale(-s, s, -s);
            }

            ItemStack stack = new ItemStack(travelData.icon());
            ItemStackRenderState itemRenderState = new ItemStackRenderState();
            minecraft.getItemModelResolver().updateForTopItem(itemRenderState, stack, ItemDisplayContext.GUI, minecraft.level, null, 0);

            poseStack.pushPose();
            {
                float s = 1.5F;
                poseStack.scale(s, s, s);
                poseStack.translate(-0.5, -0.5, -2);
                poseStack.rotateAround(Axis.ZN.rotationDegrees(45), 0.5F, 0.5F, 0.5F);

                renderBackdrop(poseStack, packedLight, ARGB.colorFromFloat(1, outlineR, outlineG, outlineB));
            }
            poseStack.popPose();

            itemRenderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, 0);
            renderFeatures();

            poseStack.popPose();
        }

        if (!travelData.name().trim().isEmpty()) {
            // Render Text
            poseStack.pushPose();

            {
                Quaternionf textRotation = Axis.YN.rotationDegrees(camera.yRot()).mul(Axis.XP.rotationDegrees(camera.xRot()));
                int lineHeight = minecraft.font.lineHeight;
                double scale = 0.1;
                double textOffsetY = scale * lineHeight + 1.25;
                if (hasIcon && active) {
                    textOffsetY += 0.15;
                }

                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(textRotation);
                poseStack.translate(0, textOffsetY, 0);
                float s = (float) scale;
                poseStack.scale(-s, -s, s);
            }

            Component textComponent = Component.literal(travelData.name().trim());

            float textOpacitySetting = minecraft.options.getBackgroundOpacity(0.5f);
            int textBg = (int) (textOpacitySetting * 255) << 24;
            float halfWidth = (float) (-minecraft.font.width(textComponent) / 2);

            Matrix4f matrix4f = poseStack.last().pose();

            minecraft.font.drawInBatch(textComponent, halfWidth, 0, textColor, false, matrix4f, TEXT_BUFFER, Font.DisplayMode.SEE_THROUGH, textBg, packedLight);
            TEXT_BUFFER.endBatch();

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static class BackdropTintingBufferSource extends MultiBufferSource.BufferSource {

        private int tintColor = -1;

        private BackdropTintingBufferSource() {
            super(new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE), Object2ObjectSortedMaps.emptyMap());
        }

        private void setTintColor(int tintColor) {
            this.tintColor = tintColor;
        }

        private void clearTintColor() {
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

    private record TintingVertexConsumer(VertexConsumer delegate, int tintColor) implements VertexConsumer {

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
