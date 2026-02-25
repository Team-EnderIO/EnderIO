package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.client.foundation.renderer.OutlineBuffer;
import com.enderio.enderio.compat.ModCompatHelper;
import com.enderio.enderio.content.travel.travel_anchor.AnchorTravelTarget;
import com.enderio.enderio.content.travel.travel_anchor.PaintedTravelAnchorBlockEntity;
import com.enderio.enderio.init.EIOBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class TravelAnchorRenderer implements TravelRenderer<AnchorTravelTarget> {
    Lazy<ArrayList<BakedQuad>> outlineQuads = Lazy.of(() -> {
        Minecraft minecraft = Minecraft.getInstance();

        Block outlineBlock = EIOBlocks.OUTLINE_BLOCK.get();
        BlockState outlineBlockState = outlineBlock.defaultBlockState();
        BakedModel outlineBlockModel = minecraft.getBlockRenderer().getBlockModel(outlineBlockState);

        RandomSource randomSource = RandomSource.create(42L);

        ArrayList<BakedQuad> bakedQuads = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            // Should be exactly 1 quad per direction
            bakedQuads.addAll(outlineBlockModel.getQuads(outlineBlockState, dir, randomSource));
        }
        return bakedQuads;
    });

    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack,
            double distanceSquared, boolean active, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (Objects.isNull(minecraft.level) || !travelData.isVisible()) {
            return;
        }

        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Vec3 towardsCamera = travelData.pos()
            .getCenter()
            .vectorTo(cameraPos)
            .normalize();

        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        int color = 0xFFFFFF;
        if (active) {
            color = ChatFormatting.GOLD.getColor() == null ? 0xFFFFFF : ChatFormatting.GOLD.getColor();
        }

        VertexConsumer solidRenderType = buffer.getBuffer(RenderType.solid());
        ModelBlockRenderer blockModelRenderer = minecraft.getBlockRenderer().getModelRenderer();

        int packedLight = LightTexture.pack(15, 15);

        // Render Model
        {
            BlockState blockState = minecraft.level.getBlockState(travelData.pos());
            if (minecraft.level.getBlockEntity(travelData.pos()) instanceof PaintedTravelAnchorBlockEntity paintedTravelAnchorBlock) {
                Optional<Block> paint = paintedTravelAnchorBlock.getPrimaryPaint();

                if (paint.isPresent()) {
                    blockState = paint.get().defaultBlockState();
                }
            }

            BakedModel blockModel = minecraft.getBlockRenderer().getBlockModel(blockState);
            blockModelRenderer.renderModel(poseStack.last(), solidRenderType, blockState, blockModel,
                1, 1, 1, packedLight, OverlayTexture.NO_OVERLAY);
        }

        // Render outline block
        {
            poseStack.pushPose();

            float outlineSize = active ? 0.2F : 0.15F;
            float scale = 1F + 2 * outlineSize;
            Vec3 offset = towardsCamera.scale(-scale).subtract(outlineSize, outlineSize, outlineSize);

            poseStack.translate(offset.x, offset.y, offset.z);
            poseStack.scale(scale, scale, scale);

            float r = ((color & (0xFF << 16)) >> 16) / 255F;
            float g = ((color & (0xFF << 8)) >> 8) / 255F;
            float b = (color & 0xFF) / 255F;

            for (var quad : outlineQuads.get()) {
                solidRenderType.putBulkData(poseStack.last(), quad, r, g, b, 1, packedLight, OverlayTexture.NO_OVERLAY);
            }

            poseStack.popPose();
        }

        // Render Text
        if (!travelData.name().trim().isEmpty()) {
            poseStack.pushPose();

            {
                double distance = Math.sqrt(distanceSquared);
                double baseScale = 0.1;
                double scale = baseScale + distance * baseScale * baseScale;
                scale = scale * (Math.sin(Math.toRadians(minecraft.options.fov().get() / 4d)));
                if (active) {
                    scale *= 1.3;
                }

                Quaternionf textRotation = Axis.YN.rotationDegrees(camera.getYRot()).mul(Axis.XP.rotationDegrees(camera.getXRot()));
                int lineHeight = minecraft.font.lineHeight;

                poseStack.translate(0.5, scale * lineHeight, 0.5);
                poseStack.mulPose(textRotation);
                poseStack.translate(0, 1.5, 0);
                float scaleF = (float) scale;
                poseStack.scale(-scaleF, -scaleF, scaleF);
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

            minecraft.font.drawInBatch(textComponent, halfWidth, 0, color, false, matrix4f, buffer, mode1, textBg1, packedLight);
            minecraft.font.drawInBatch(textComponent, halfWidth, 0, color, false, matrix4f, buffer, mode2, textBg2, packedLight);

            poseStack.popPose();
        }

        // Render Icon
        if (travelData.icon() != Items.AIR) {
            poseStack.pushPose();

            double scale = Math.sqrt(Math.sqrt(distanceSquared));
            scale = scale * (Math.sin(Math.toRadians(minecraft.options.fov().get() / 4d)));
            if (active) {
                scale *= 1.3;
            }

            Vector3f upDir = new Vec3(0, 1, 0)
                .xRot(-camera.getXRot() * ((float) Math.PI / 180F))
                .yRot(-camera.getYRot() * ((float) Math.PI / 180F))
                .toVector3f();
            Vector3f direction = towardsCamera.toVector3f();
            Quaternionf iconRotation = new Quaternionf().lookAlong(direction.x(), direction.y(), direction.z(), upDir.x(), upDir.y(), upDir.z());

            Vec3 offset = towardsCamera.scale(0.9);

            poseStack.translate(offset.x() + 0.5, offset.y() + 0.5, offset.z() + 0.5);
            poseStack.mulPose(iconRotation.invert());
            float scaleF = (float) scale;
            poseStack.scale(-scaleF, scaleF, -scaleF);

            ItemStack stack = new ItemStack(travelData.icon());
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            minecraft
                .getItemRenderer()
                .render(stack, ItemDisplayContext.GUI, true, poseStack, OutlineBuffer.INSTANCE, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);

            poseStack.popPose();
        }
    }
}
