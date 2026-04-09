package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.client.EIOPipelineModifiers;
import com.enderio.enderio.client.EnderIOClient;
import com.enderio.enderio.client.foundation.renderer.OutlineBuffer;
import com.enderio.enderio.compat.ModCompatHelper;
import com.enderio.enderio.content.travel.travel_anchor.AnchorTravelTarget;
import com.enderio.enderio.content.travel.travel_anchor.PaintedTravelAnchorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
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

    public static FeatureRenderDispatcher FEATURE = new FeatureRenderDispatcher(
        new SubmitNodeStorage(),
        Minecraft.getInstance().getModelManager(),
        Minecraft.getInstance().renderBuffers().bufferSource(),
        Minecraft.getInstance().getAtlasManager(),
        Minecraft.getInstance().renderBuffers().outlineBufferSource(),
        Minecraft.getInstance().renderBuffers().crumblingBufferSource(),
        Minecraft.getInstance().font,
        Minecraft.getInstance().gameRenderer.getGameRenderState()
    );

    public static SubmitNodeStorage NODE = FEATURE.getSubmitNodeStorage();


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

        {
            double anchorScale = (float) Math.sqrt(distanceToCamera);
            if (active) {
                anchorScale = anchorScale * 1.3;
            }
            anchorScale = anchorScale * (Math.sin(Math.toRadians(minecraft.options.fov().get() / 4d)));
            anchorScale = Math.max(anchorScale, 1.0F);

            poseStack.translate(0.5, 0.5, 0.5);
            float s = (float) anchorScale;
            poseStack.scale(s, s, s);
            poseStack.translate(-0.5, -0.5, -0.5);
        }

        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        int outlineColor = 0xFF0B4D42;
        int textColor = 0xFFFFFFFF;
        if (active) {
            textColor = ChatFormatting.AQUA.getColor() == null ? 0xFFFFFF : ChatFormatting.AQUA.getColor();
            outlineColor = textColor;
        }
        float outlineR = ((outlineColor & (0xFF << 16)) >> 16) / 255F;
        float outlineG = ((outlineColor & (0xFF << 8)) >> 8) / 255F;
        float outlineB = (outlineColor & 0xFF) / 255F;

        int packedLight = LightCoordsUtil.pack(15, 15);

        boolean hasIcon = travelData.icon() != Items.AIR;

        if (!hasIcon) {
            // Render Model
            {
                BlockState blockState = minecraft.level.getBlockState(travelData.pos());
                if (minecraft.level.getBlockEntity(travelData.pos()) instanceof PaintedTravelAnchorBlockEntity paintedTravelAnchorBlock) {
                    Optional<Block> paint = paintedTravelAnchorBlock.getPrimaryPaint();

                    if (paint.isPresent()) {
                        blockState = paint.get().defaultBlockState();
                    }
                }

                BlockModelRenderState modelRenderState = new BlockModelRenderState();

                BlockDisplayContext context = BlockDisplayContext.create();
                Minecraft.getInstance().getBlockModelResolver().update(modelRenderState, blockState, context);

                modelRenderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, 0);
                FEATURE.renderAllFeatures();
                Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
            }

            // Render outline block
            {
                poseStack.pushPose();

                float outlineSize = active ? 0.2F : 0.15F;
                float scale = 1F + 2 * outlineSize;
                Vec3 offset = towardsCamera.scale(-scale).subtract(outlineSize, outlineSize, outlineSize);

                poseStack.translate(offset.x, offset.y, offset.z);
                poseStack.scale(scale, scale, scale);

                BlockModelRenderState renderState = new BlockModelRenderState();
                BACKDROP.get().update(renderState, Blocks.AIR.defaultBlockState(), BlockDisplayContext.create(), 42);
                renderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, ARGB.colorFromFloat(1, outlineR, outlineG, outlineB));

                RenderSystem.pushPipelineModifier(EIOPipelineModifiers.FORCE_NO_DEPTH);
                FEATURE.renderAllFeatures();
                Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
                RenderSystem.popPipelineModifier();

                poseStack.popPose();
            }

            {
                BlockState blockState = minecraft.level.getBlockState(travelData.pos());
                if (minecraft.level.getBlockEntity(travelData.pos()) instanceof PaintedTravelAnchorBlockEntity paintedTravelAnchorBlock) {
                    Optional<Block> paint = paintedTravelAnchorBlock.getPrimaryPaint();

                    if (paint.isPresent()) {
                        blockState = paint.get().defaultBlockState();
                    }
                }

                BlockModelRenderState modelRenderState = new BlockModelRenderState();

                BlockDisplayContext context = BlockDisplayContext.create();
                Minecraft.getInstance().getBlockModelResolver().update(modelRenderState, blockState, context);

                modelRenderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, 0);
                FEATURE.renderAllFeatures();
                Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
            }

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
//            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
//            minecraft
//                .getItemRenderer()
//                .render(stack, ItemDisplayContext.GUI, false, poseStack, OutlineBuffer.INSTANCE, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);

            float s = 1.5F;
            poseStack.scale(s, s, s);
            poseStack.translate(-0.5, -0.5, -2);
            poseStack.rotateAround(Axis.ZN.rotationDegrees(45), 0.5F, 0.5F, 0.5F);

            BlockModelRenderState renderState = new BlockModelRenderState();
            BACKDROP.get().update(renderState, Blocks.AIR.defaultBlockState(), BlockDisplayContext.create(), 42);
            renderState.submit(poseStack, NODE, packedLight, OverlayTexture.NO_OVERLAY, ARGB.colorFromFloat(1, outlineR, outlineG, outlineB));

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

            var mode1 = Font.DisplayMode.SEE_THROUGH;
            var mode2 = Font.DisplayMode.NORMAL;
            int textBg1 = textBg;
            int textBg2 = 0;

            if (ModCompatHelper.hasIris()) {
                // required for text to render correctly, possibly an Iris bug
                // is there a better way to draw text?
                var _m = mode1;
                mode1 = mode2;
                mode2 = _m;

                int _t = textBg1;
                textBg1 = textBg2;
                textBg2 = _t;
            }
            Matrix4f matrix4f = poseStack.last().pose();

            minecraft.font.drawInBatch(textComponent, halfWidth, 0, textColor, false, matrix4f, buffer, mode1, textBg1, packedLight);
            minecraft.font.drawInBatch(textComponent, halfWidth, 0, textColor, false, matrix4f, buffer, mode2, textBg2, packedLight);

            poseStack.popPose();
        }

        poseStack.popPose();
        // Seems to work fine without this line, but leaving it here in case there are future buffer problems
        // minecraft.renderBuffers().bufferSource().endBatch();
    }
}
