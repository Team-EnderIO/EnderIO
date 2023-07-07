package com.enderio.machines.client.rendering.blockentity;

import com.enderio.EnderIO;
import com.enderio.core.client.RenderUtil;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.DisplayMode;
import com.enderio.machines.common.blockentity.multienergy.ICapacityTier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2i;

public class CapacitorBankBER implements BlockEntityRenderer<CapacitorBankBlockEntity> {

    private static final ResourceLocation FULL_BAR = EnderIO.loc("block/capacitor_additionals/capacitor_bank_bar_full");
    private static final ResourceLocation END_BAR = EnderIO.loc("block/capacitor_additionals/capacitor_bank_bar_end");
    private static final ResourceLocation ENERGY_BAR = EnderIO.loc("block/capacitor_additionals/capacitor_bank_bar_energy");

    private static final ResourceLocation IO_1x1 = EnderIO.loc("block/capacitor_additionals/1x1_full");
    private static final ResourceLocation IO_FULL = EnderIO.loc("block/capacitor_additionals/full");

    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public CapacitorBankBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CapacitorBankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
        int packedOverlay) {

        if (blockEntity.getLevel() != null) {
            for (Direction direction: HORIZONTAL_DIRECTIONS) {
                BlockPos facingPos = blockEntity.getBlockPos().relative(direction);
                if (Block.shouldRenderFace(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos(), direction, facingPos)) {
                    DisplayMode mode = blockEntity.getDisplayMode(direction);
                    if (mode == DisplayMode.BAR) {
                        renderBar(blockEntity, poseStack, bufferSource, direction);
                    }
                    if (mode == DisplayMode.IO) {
                        renderIO(blockEntity, poseStack, bufferSource, direction);
                    }
                }
            }
        }
    }

    private static void renderBar(CapacitorBankBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, Direction facing) {
        if (getDisplayModeRelative(blockEntity, facing, new Vector2i(0,-1)) == DisplayMode.BAR) {
            return;
        }
        int length = 1;
        //some random limit
        while(length < 16) {
            if (getDisplayModeRelative(blockEntity, facing, new Vector2i(0, length)) == DisplayMode.BAR) {
                length++;
            } else {
                break;
            }
        }
        for (int i = 0; i < length; i++) {
            poseStack.pushPose();
            poseStack.translate(0, -i, 0);
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
            PoseStack.Pose last = poseStack.last();
            renderTopHalf(last.pose(), last.normal(), buffer, facing, i == 0);
            renderBottomHalf(last.pose(), last.normal(), buffer, facing, i == length - 1);
            poseStack.popPose();
        }
        if (blockEntity.getEnergyStorage().getMaxEnergyStored() > 0) {
            float f = blockEntity.getEnergyStorage().getEnergyStored() / (float) blockEntity.getEnergyStorage().getMaxEnergyStored();
            int filledPixels = Math.round(f * (10 + (length-1)*16));
            for (int i = length - 1; i >= 0; i--) {
                if (filledPixels <= 0)
                    break;
                poseStack.pushPose();
                poseStack.translate(0, -i, 0);
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
                PoseStack.Pose last = poseStack.last();
                boolean isBottomEnd = i == length - 1;
                renderEnergy(last.pose(), last.normal(), buffer, facing, filledPixels, isBottomEnd);
                filledPixels -= isBottomEnd ? 13 : 16;
                poseStack.popPose();
            }
        }
    }
    private static void renderIO(CapacitorBankBlockEntity capacitorBank, PoseStack poseStack, MultiBufferSource bufferSource, Direction facing) {
        int light = LightTexture.FULL_BRIGHT;
        if (capacitorBank.getLevel() != null)
            light = LevelRenderer.getLightColor(capacitorBank.getLevel(), capacitorBank.getBlockPos().relative(facing));
        Size size = findSize(capacitorBank, facing);
        renderTexture(poseStack.last().pose(), poseStack.last().normal(), bufferSource.getBuffer(RenderType.cutout()), facing, size.getTexture(), light);
        if ((-size.x0 == size.x1 || -size.x0 +1 == size.x1)
            && (-size.y0 == size.y1 || -size.y0 +1 == size.y1)) {
            int height = size.y1 - size.y0 + 1;
            int width = size.x1 - size.x0 + 1;
            boolean isLongInformation = height > 1.4f * width;
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YN.rotationDegrees(facing.get2DDataValue()*90));
            poseStack.translate(width%2 == 0 ? 0.5f : 0, height%2 == 0 ? -0.5f : 0,0.502f);
            poseStack.scale(1/16f,-1/16f,1/16f);
            Font font = Minecraft.getInstance().font;
            float fontWidthScale = width / (float)font.width("OutputOutput");
            float fontHeightScale = height / (float)(font.lineHeight * 5);
            float scale = Math.min(fontWidthScale, fontHeightScale) * 16;
            poseStack.scale(scale,scale,scale);
            if (!isLongInformation) {
                poseStack.pushPose();
                poseStack.translate(-font.width("I/0") / 2f, -font.lineHeight * 1.5f, 0);
                font.drawInBatch("I/0", 0, 0, 0xFF000000, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light, false);
                poseStack.popPose();
                poseStack.pushPose();
                long energySurplus = capacitorBank.getAddedEnergy() - capacitorBank.getRemovedEnergy();
                int color = 0;
                if (energySurplus > 0)
                    color = 0xFF00FF00;
                if (energySurplus < 0)
                    color = 0xFFFF0000;
                poseStack.translate(-font.width(String.valueOf(energySurplus)) / 2f, font.lineHeight * 0.5f, 0);
                font.drawInBatch(String.valueOf(energySurplus), 0, 0, color, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light, false);
                poseStack.popPose();
            } else {
                poseStack.pushPose();
                poseStack.translate(-font.width("Input") / 2f, -font.lineHeight * 3.5f, 0);
                font.drawInBatch("Input", 0, 0, 0xFF000000, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light, false);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(-font.width(String.valueOf(capacitorBank.getAddedEnergy())) / 2f, -font.lineHeight * 1.5f, 0);
                font.drawInBatch(String.valueOf(capacitorBank.getAddedEnergy()), 0, 0, 0xFF00FF00, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light, false);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(-font.width("Output") / 2f, font.lineHeight * 0.5f, 0);
                font.drawInBatch("Output", 0, 0, 0xFF000000, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light, false);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(-font.width(String.valueOf(capacitorBank.getRemovedEnergy())) / 2f, font.lineHeight * 2.5f, 0);
                font.drawInBatch(String.valueOf(capacitorBank.getRemovedEnergy()), 0, 0, 0xFFFF0000, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light, false);
                poseStack.popPose();

            }
            poseStack.popPose();
        }
    }

    /**
     * relative is bottom-right positive
     */
    private static DisplayMode getDisplayModeRelative(CapacitorBankBlockEntity blockEntity, Direction horizontalFacing, Vector2i relative) {
        if (blockEntity.getLevel() == null)
            return DisplayMode.NONE;
        return getDisplayModeRelative(blockEntity.getLevel(), horizontalFacing, blockEntity.getBlockPos(), relative, blockEntity.tier);
    }
    private static DisplayMode getDisplayModeRelative(Level level, Direction horizontalFacing, BlockPos pos, Vector2i relative, ICapacityTier tier) {
        pos = pos.below(relative.y());
        pos = pos.relative(horizontalFacing.getClockWise(), -relative.x);
        if (level.getBlockEntity(pos) instanceof CapacitorBankBlockEntity capacitorBank && capacitorBank.tier == tier) {
            return capacitorBank.getDisplayMode(horizontalFacing);
        }
        return DisplayMode.NONE;
    }
    public static void renderBottomHalf(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, Direction facing, boolean isEnd) {
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(isEnd ? END_BAR : FULL_BAR);
        float inset = -0.001F;
        RenderUtil.renderFace(facing, pose, normal, consumer, texture, 0, 0, inset, 1, 0.5f, 0xFFFFFFFF);
    }
    public static void renderTopHalf(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, Direction facing, boolean isEnd) {
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(isEnd ? END_BAR : FULL_BAR);
        float inset = -0.001F;
        RenderUtil.renderFace(facing, pose, normal, consumer, texture, 0, 0.5f, inset, 1, 0.5f, 0xFFFFFFFF);
    }
    public static void renderEnergy(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, Direction facing, int pixels, boolean isBottomEnd) {
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ENERGY_BAR);
        float inset = -0.002F;
        RenderUtil.renderFace(facing, pose, normal, consumer, texture, 0, isBottomEnd ? 3/16f : 0, inset, 1, Math.min(pixels, isBottomEnd ? 13 : 16)/16f, 0xFFFFFFFF);
    }
    public static void renderTexture(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, Direction facing, ResourceLocation rl, int light) {
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(rl);
        float inset = -0.001F;
        RenderUtil.renderFace(facing, pose, normal, consumer, texture, 0, 0, inset, 1, 1, 0xFFFFFFFF, light);
    }

    @Override
    public int getViewDistance() {
        return 92;
    }

    //TODO test performance, potentially try to cache
    private static Size findSize(CapacitorBankBlockEntity capacitorBank, Direction facing) {
        int x0 = 0;
        int y0 = 0;
        int x1 = 0;
        int y1 = 0;
        //expand right
        for (int i = 1; i < 16; i++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(i, 0)) != DisplayMode.IO)
                break;
            x1 = i;
        }
        //expand left
        for (int i = 1; i < 16 - x1; i++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(-i, 0)) != DisplayMode.IO)
                break;
            x0 = -i;
        }
        //expand down
        downSearch:
        for (int i = 1; i < 16; i++) {
            int checked = 0;
            for (int x = x0; x <= x1; x++) {
                if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(x, i)) == DisplayMode.IO) {
                    checked++;
                } else {
                    if (checked > 0)
                        return new Size(0,0,0,0);
                    break downSearch;
                }
            }
            y1 = i;

        }
        //expand up
        upSearch:
        for (int i = 1; i < 16 -y1; i++) {
            int checked = 0;
            for (int x = x0; x <= x1; x++) {
                if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(x, -i)) == DisplayMode.IO) {
                    checked++;
                } else {
                    if (checked > 0)
                        return new Size(0,0,0,0);
                    break upSearch;
                }
            }
            y0 = -i;

        }

        return validateSize(new Size(x0, y0, x1, y1), capacitorBank, facing);
    }

    private static Size validateSize(Size size, CapacitorBankBlockEntity capacitorBank, Direction facing) {
        for (int x = size.x0; x <= size.x1; x++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(x, size.y0-1)) == DisplayMode.IO)
                return new Size(0,0, 0, 0);
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(x, size.y1+1)) == DisplayMode.IO)
                return new Size(0,0, 0, 0);
        }
        for (int y = size.y0; y <= size.y1; y++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(size.x0-1, y)) == DisplayMode.IO)
                return new Size(0,0, 0, 0);
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(size.x1+1, y)) == DisplayMode.IO)
                return new Size(0,0, 0, 0);
        }
        return size;
    }

    private record Size(int x0, int y0, int x1, int y1) {
        ResourceLocation getTexture() {
            boolean isLeft = x0 == 0;
            boolean isRight = x1 == 0;
            boolean isSmallX = isLeft && isRight;
            boolean isUp = y0 == 0;
            boolean isDown = y1 == 0;
            boolean isSmallY = isUp && isDown;
            if (isSmallX && isSmallY)
                return IO_1x1;
            if (isSmallY) {
                if (isLeft)
                    return EnderIO.loc("block/capacitor_additionals/small_r");
                if (isRight)
                    return EnderIO.loc("block/capacitor_additionals/small_l");
                return EnderIO.loc("block/capacitor_additionals/small_lr");
            }
            if (isSmallX) {
                if (isUp)
                    return EnderIO.loc("block/capacitor_additionals/small_u");
                if (isDown)
                    return EnderIO.loc("block/capacitor_additionals/small_d");
                return EnderIO.loc("block/capacitor_additionals/small_ud");
            }
            if (isUp) {
                if (isLeft)
                    return EnderIO.loc("block/capacitor_additionals/corner_tr");
                if (isRight)
                    return EnderIO.loc("block/capacitor_additionals/corner_tl");
                return EnderIO.loc("block/capacitor_additionals/side_t");
            }
            if (isDown) {
                if (isLeft)
                    return EnderIO.loc("block/capacitor_additionals/corner_br");
                if (isRight)
                    return EnderIO.loc("block/capacitor_additionals/corner_bl");
                return EnderIO.loc("block/capacitor_additionals/side_b");
            }
            if (isLeft)
                return EnderIO.loc("block/capacitor_additionals/side_r");
            if (isRight)
                return EnderIO.loc("block/capacitor_additionals/side_l");
            return IO_FULL;
        }
    }
}
