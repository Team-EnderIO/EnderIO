package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.client.foundation.renderer.OutlineBuffer;
import com.enderio.enderio.content.travel.travel_anchor.AnchorTravelTarget;
import com.enderio.enderio.content.travel.travel_anchor.PaintedTravelAnchorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Optional;

public class TravelAnchorRenderer implements TravelRenderer<AnchorTravelTarget> {
    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack,
            double distanceSquared, boolean active, float partialTick) {
        if (!travelData.isVisible()) {
            return;
        }

        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);

        Vec2 playerLookRotation = player.getRotationVector();
        Vec3 playerEyePosition = player.getEyePosition(partialTick);
        Vec3 playerOffset = travelData.pos().getCenter().vectorTo(playerEyePosition);
        Vec3 playerOffsetNormalized = playerOffset.normalize();

        poseStack.pushPose();
        poseStack.translate(travelData.pos().getX(), travelData.pos().getY(), travelData.pos().getZ());
        Minecraft minecraft = Minecraft.getInstance();
        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        int color = 0xFFFFFF;
        if (active) {
            color = ChatFormatting.GOLD.getColor() == null ? 0xFFFFFF : ChatFormatting.GOLD.getColor();
        }

        // Render Model
        BlockState blockState = minecraft.level.getBlockState(travelData.pos());
        if (minecraft.level
                .getBlockEntity(travelData.pos()) instanceof PaintedTravelAnchorBlockEntity paintedTravelAnchorBlock) {
            Optional<Block> paint = paintedTravelAnchorBlock.getPrimaryPaint();

            if (paint.isPresent()) {
                blockState = paint.get().defaultBlockState();
            }
        }

        BakedModel blockModel = minecraft.getBlockRenderer().getBlockModel(blockState);
        VertexConsumer solid = buffer.getBuffer(RenderType.solid());
        minecraft.getBlockRenderer()
                .getModelRenderer()
                .renderModel(poseStack.last(), solid, blockState, blockModel, 1, 1, 1, 0xF000F0,
                        OverlayTexture.NO_OVERLAY);

        // Render background outline
        Block backgroundBlock = active ? Blocks.YELLOW_CONCRETE : Blocks.WHITE_CONCRETE;
        BlockState bgBlockState = backgroundBlock.defaultBlockState();
        BakedModel bgBlockModel = minecraft.getBlockRenderer().getBlockModel(bgBlockState);

        float bgBorderSize = active ? 0.2F : 0.15F;
        float ssf = 1F + 2 * bgBorderSize;
        Vec3 po = playerOffsetNormalized.scale(-ssf).subtract(bgBorderSize, bgBorderSize, bgBorderSize);
        poseStack.pushPose();
        poseStack.translate(po.x, po.y, po.z);
        poseStack.scale(ssf, ssf, ssf);

        minecraft
            .getBlockRenderer()
            .getModelRenderer()
            .renderModel(poseStack.last(), solid, bgBlockState, bgBlockModel, 1, 1, 1, 0xF000F0, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        // Render Text
        if (!travelData.name().trim().isEmpty()) {
            // Scale for rendering
            double scale = Math.sqrt(0.0035 * Math.sqrt(distanceSquared));
            if (scale < 0.1f) {
                scale = 0.1f;
            }
            scale = scale * (Math.sin(Math.toRadians(Minecraft.getInstance().options.fov().get() / 4d)));
            if (active) {
                scale *= 1.3;
            }

            Quaternionf textRotation = Axis.YN.rotationDegrees(playerLookRotation.y)
                .mul(Axis.XP.rotationDegrees(playerLookRotation.x));
            Vec3 offset = playerOffsetNormalized.scale(1.25);
            int lineHeight = Minecraft.getInstance().font.lineHeight;

            poseStack.pushPose();
            poseStack.translate(offset.x() + 0.5,
                    offset.y() + (scale * lineHeight), offset.z() + 0.5);
            poseStack.mulPose(textRotation);
            poseStack.translate(0, 1.5, 0);
            float scaleF = (float) scale;
            poseStack.scale(-scaleF, -scaleF, scaleF);

            Matrix4f matrix4f = poseStack.last().pose();
            Component tc = Component.literal(travelData.name().trim());

            float textOpacitySetting = minecraft.options.getBackgroundOpacity(0.5f);
            int alpha = (int) (textOpacitySetting * 255) << 24;
            float halfWidth = (float) (-minecraft.font.width(tc) / 2);

            minecraft.font.drawInBatch(tc, halfWidth, 0, color, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0,
                    LightTexture.pack(15, 15));
            minecraft.font.drawInBatch(tc, halfWidth, 0, color, false, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH,
                    alpha, LightTexture.pack(15, 15));
            poseStack.popPose();
        }

        // Render Icon
        if (travelData.icon() != Items.AIR) {
            // Scale for rendering
            double scale = Math.sqrt(Math.sqrt(distanceSquared));
            scale = scale * (Math.sin(Math.toRadians(Minecraft.getInstance().options.fov().get() / 4d)));
            if (active) {
                scale *= 1.3;
            }

            Vector3f upDir = new Vec3(0, 1, 0)
                .xRot(-playerLookRotation.x * ((float) Math.PI / 180F))
                .yRot(-playerLookRotation.y * ((float) Math.PI / 180F))
                .toVector3f();
            Vector3f direction = playerOffsetNormalized.toVector3f();
            Quaternionf iconRotation = new Quaternionf().lookAlong(direction.x(), direction.y(), direction.z(), upDir.x(), upDir.y(), upDir.z());
            Vec3 offset = playerOffsetNormalized.scale(0.9);

            poseStack.pushPose();
            poseStack.translate(offset.x() + 0.5, offset.y() + 0.5, offset.z() + 0.5);
            poseStack.mulPose(iconRotation.invert());
            float scaleF = (float) scale;
            poseStack.scale(-scaleF, scaleF, -scaleF);

            ItemStack stack = new ItemStack(travelData.icon());
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            minecraft.getItemRenderer()
                    .render(stack, ItemDisplayContext.GUI, true, poseStack, OutlineBuffer.INSTANCE, 15728880,
                            OverlayTexture.NO_OVERLAY, bakedmodel);
            poseStack.popPose();
        }

        poseStack.popPose();
        minecraft.renderBuffers().bufferSource().endBatch();

    }
}
