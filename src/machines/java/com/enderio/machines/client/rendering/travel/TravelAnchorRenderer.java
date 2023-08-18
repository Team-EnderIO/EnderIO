package com.enderio.machines.client.rendering.travel;

import com.enderio.api.travel.TravelRenderer;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class TravelAnchorRenderer implements TravelRenderer<AnchorTravelTarget> {
    public static final RenderType BOLD_LINES = OutlineRenderType.createLines("bold_lines", 3);
    public static final RenderType VERY_BOLD_LINES = OutlineRenderType.createLines("very_bold_lines", 5);

    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack, double distanceSquared, boolean active) {
        if (!travelData.getVisibility())
            return;
        poseStack.pushPose();
        poseStack.translate(travelData.getPos().getX(), travelData.getPos().getY(), travelData.getPos().getZ());
        Minecraft minecraft = Minecraft.getInstance();
        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        int color = 0xFFFFFF;
        if (active) {
            color = ChatFormatting.GOLD.getColor() == null ? 0xFFFFFF : ChatFormatting.GOLD.getColor();
        }

        // Render Model
        BlockState blockState = minecraft.level.getBlockState(travelData.getPos());
        BakedModel blockModel = minecraft.getBlockRenderer().getBlockModel(blockState);
        VertexConsumer solid = buffer.getBuffer(RenderType.solid());
        minecraft
            .getBlockRenderer()
            .getModelRenderer()
            .renderModel(poseStack.last(), solid, blockState, blockModel, 1, 1, 1, 0xF000F0, OverlayTexture.NO_OVERLAY);

        // Render line
        RenderType lineType;
        if (distanceSquared > 85 * 85) {
            lineType = RenderType.lines();
        } else if (distanceSquared > 38 * 38) {
            lineType = BOLD_LINES;
        } else {
            lineType = VERY_BOLD_LINES;
        }
        VertexConsumer lines = buffer.getBuffer(lineType);
        LevelRenderer.renderLineBox(poseStack, lines, 0, 0, 0, 1, 1, 1, FastColor.ARGB32.red(color) / 255F, FastColor.ARGB32.green(color) / 255F,
            FastColor.ARGB32.blue(color) / 255F, 1);


        // Render Text
        if (!travelData.getName().trim().isEmpty()) {
            // Scale for rendering
            double doubleScale = Math.sqrt(0.0035 * Math.sqrt(distanceSquared));
            if (doubleScale < 0.1f) {
                doubleScale = 0.1f;
            }
            doubleScale = doubleScale * (Math.sin(Math.toRadians(Minecraft.getInstance().options.fov().get() / 4d)));
            if (active) {
                doubleScale *= 1.3;
            }
            float scale = (float) doubleScale;

            poseStack.pushPose();
            poseStack.translate(0.5, 1.05 + (doubleScale * Minecraft.getInstance().font.lineHeight), 0.5);
            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            poseStack.scale(-scale, -scale, scale);

            Matrix4f matrix4f = poseStack.last().pose();
            Component tc = Component.literal(travelData.getName().trim());

            float textOpacitySetting = minecraft.options.getBackgroundOpacity(0.5f);
            int alpha = (int) (textOpacitySetting * 255) << 24;
            float halfWidth = (float) (-minecraft.font.width(tc) / 2);

            minecraft.font.drawInBatch(tc, halfWidth, 0, color, false, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH, alpha, LightTexture.pack(15, 15));
            minecraft.font.drawInBatch(tc, halfWidth, 0, color, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.pack(15, 15));
            poseStack.popPose();
        }

        //         Render Icon
        if (travelData.getIcon() != Items.AIR) {
            // Scale for rendering
            double doubleScale = Math.sqrt(Math.sqrt(distanceSquared));
            doubleScale = doubleScale * (Math.sin(Math.toRadians(Minecraft.getInstance().options.fov().get() / 4d)));
            if (active) {
                doubleScale *= 1.3;
            }
            float scale = (float) doubleScale;
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            poseStack.translate(0, 0, -1);
            poseStack.scale(scale, scale, scale);
            ItemStack stack = new ItemStack(travelData.getIcon());
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            minecraft
                .getItemRenderer()
                .render(stack, ItemDisplayContext.GUI, false, poseStack, OutlineBuffer.INSTANCE, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
            poseStack.popPose();
        }

        poseStack.popPose();
        minecraft.renderBuffers().bufferSource().endBatch();

    }
}
