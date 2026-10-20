package com.enderio.enderio.client.content.machines.renderer.blockentity;

import com.enderio.core.client.RenderUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankBlockEntity;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankManager;
import com.enderio.enderio.content.machines.capacitor_bank.DisplayMode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

public class CapacitorBankBER implements BlockEntityRenderer<CapacitorBankBlockEntity, CapacitorBankRenderState> {

    private static final Identifier FULL_BAR = EnderIO.id("block/capacitor_additionals/capacitor_bank_bar_full");
    private static final Identifier END_BAR = EnderIO.id("block/capacitor_additionals/capacitor_bank_bar_end");
    private static final Identifier ENERGY_BAR = EnderIO.id("block/capacitor_additionals/capacitor_bank_bar_energy");

    private static final Identifier IO_1X1 = EnderIO.id("block/capacitor_additionals/1x1_full");
    private static final Identifier IO_FULL = EnderIO.id("block/capacitor_additionals/full");

    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public CapacitorBankBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void extractRenderState(CapacitorBankBlockEntity blockEntity, CapacitorBankRenderState state, float partialTicks, Vec3 cameraPosition,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        CapacitorBankRenderState.extractBase(blockEntity, state, breakProgress);
        state.blockState = blockEntity.getBlockState();
        state.level = blockEntity.getLevel();
        state.uuid = blockEntity.getUuid();
        for (Direction direction : Direction.values()) {
            state.addDisplaymode(direction, blockEntity.getDisplayMode(direction));
        }
    }

    @Override
    public CapacitorBankRenderState createRenderState() {
        return new CapacitorBankRenderState();
    }

    @Override
    public void submit(CapacitorBankRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
        CameraRenderState cameraRenderState) {

        if (blockEntityRenderState.level != null) {
            for (Direction direction: HORIZONTAL_DIRECTIONS) {
                BlockPos facingPos = blockEntityRenderState.blockPos.relative(direction);
                if (Block.shouldRenderFace(blockEntityRenderState.level, blockEntityRenderState.blockPos, blockEntityRenderState.blockState,
                    blockEntityRenderState.level.getBlockState(facingPos), direction)) {
                    DisplayMode mode = blockEntityRenderState.getDisplaymode(direction);
                    if (mode == DisplayMode.BAR) {
                        renderBar(blockEntityRenderState, poseStack, submitNodeCollector, direction);
                    }
                    if (mode == DisplayMode.IO) {
                        renderIO(blockEntityRenderState, poseStack, submitNodeCollector, direction);
                    }
                }
            }
        }
    }

    private static void renderBar(CapacitorBankRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Direction facing) {
        if (getDisplayModeRelative(blockEntityRenderState, facing, new Vector2i(0,-1)) == DisplayMode.BAR) {
            return;
        }
        int length = 1;
        //some random limit
        while(length < 16) {
            if (getDisplayModeRelative(blockEntityRenderState, facing, new Vector2i(0, length)) == DisplayMode.BAR) {
                length++;
            } else {
                break;
            }
        }
        for (int i = 0; i < length; i++) {
            poseStack.pushPose();
            poseStack.translate(0, -i, 0);
            renderTopHalf(poseStack, submitNodeCollector, facing, i == 0);
            renderBottomHalf(poseStack, submitNodeCollector, facing, i == length - 1);
            poseStack.popPose();
        }
        CapacitorBankManager.CapacitorSyncData data = CapacitorBankManager.getData(blockEntityRenderState.uuid);
        if (data != null && data.capacity() > 0) {
            float f = data.storedEnergy() / (float) data.capacity();
            int filledPixels = Math.round(f * (10 + (length-1)*16));
            for (int i = length - 1; i >= 0; i--) {
                if (filledPixels <= 0) {
                    break;
                }

                poseStack.pushPose();
                poseStack.translate(0, -i, 0);
                boolean isBottomEnd = i == length - 1;
                renderEnergy(poseStack, submitNodeCollector, facing, filledPixels, isBottomEnd);
                filledPixels -= isBottomEnd ? 13 : 16;
                poseStack.popPose();
            }
        }
    }
    private static void renderIO(CapacitorBankRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Direction facing) {
        int light = LightCoordsUtil.FULL_BRIGHT;
        if (blockEntityRenderState.level != null) {
            light = LightCoordsUtil.getLightCoords(blockEntityRenderState.level, blockEntityRenderState.blockPos.relative(facing));
        }

        CapacitorBankManager.CapacitorSyncData data = CapacitorBankManager.getData(blockEntityRenderState.uuid);
        if (data == null) {
            return;
        }

        Size size = findSize(blockEntityRenderState, facing);
        renderTexture(poseStack, submitNodeCollector, facing, size.getTexture(), light);
        if ((-size.x0 == size.x1 || -size.x0 +1 == size.x1)
            && (-size.y0 == size.y1 || -size.y0 +1 == size.y1)) {
            int height = size.y1 - size.y0 + 1;
            int width = size.x1 - size.x0 + 1;
            boolean isLongInformation = height > 1.4f * width;
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.rotate(Axis.YN.rotationDegrees(facing.get2DDataValue()*90));
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
                submitNodeCollector.submitText(poseStack, 0, 0, Component.literal("I/0").getVisualOrderText(), false, Font.DisplayMode.NORMAL,
                    light, CommonColors.BLACK, 0, 0);
                poseStack.popPose();
                poseStack.pushPose();
                long energySurplus = data.added() - data.send();
                int color = 0;
                if (energySurplus > 0) {
                    color = CommonColors.GREEN;
                }

                if (energySurplus < 0) {
                    color = CommonColors.RED;
                }

                poseStack.translate(-font.width(String.valueOf(energySurplus)) / 2f, font.lineHeight * 0.5f, 0);
                submitNodeCollector.submitText(poseStack, 0, 0, Component.literal(String.valueOf(energySurplus)).getVisualOrderText(), false, Font.DisplayMode.NORMAL,
                    light, color, 0, 0);
                poseStack.popPose();
            } else {
                poseStack.pushPose();
                poseStack.translate(-font.width("Input") / 2f, -font.lineHeight * 3.5f, 0);
                submitNodeCollector.submitText(poseStack, 0, 0, Component.literal("Input").getVisualOrderText(), false, Font.DisplayMode.NORMAL,
                    light, CommonColors.BLACK, 0, 0);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(-font.width(String.valueOf(data.added())) / 2f, -font.lineHeight * 1.5f, 0);
                submitNodeCollector.submitText(poseStack, 0, 0, Component.literal(String.valueOf(data.added())).getVisualOrderText(), false, Font.DisplayMode.NORMAL,
                    light, CommonColors.GREEN, 0, 0);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(-font.width("Output") / 2f, font.lineHeight * 0.5f, 0);
                submitNodeCollector.submitText(poseStack, 0, 0, Component.literal("Output").getVisualOrderText(), false, Font.DisplayMode.NORMAL,
                    light, CommonColors.BLACK, 0, 0);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(-font.width(String.valueOf(data.send())) / 2f, font.lineHeight * 2.5f, 0);
                submitNodeCollector.submitText(poseStack, 0, 0, Component.literal(String.valueOf(data.send())).getVisualOrderText(), false, Font.DisplayMode.NORMAL,
                    light, CommonColors.RED, 0, 0);
                poseStack.popPose();

            }
            poseStack.popPose();
        }
    }

    /**
     * relative is bottom-right positive
     */
    private static DisplayMode getDisplayModeRelative(CapacitorBankRenderState blockEntityRenderState, Direction horizontalFacing, Vector2i relative) {
        if (blockEntityRenderState.level == null) {
            return DisplayMode.NONE;
        }

        return getDisplayModeRelative(blockEntityRenderState.level, horizontalFacing, blockEntityRenderState.blockPos, relative);
    }

    private static DisplayMode getDisplayModeRelative(Level level, Direction horizontalFacing, BlockPos pos, Vector2i relative) {
        pos = pos.below(relative.y());
        pos = pos.relative(horizontalFacing.getClockWise(), -relative.x);
        if (level.getBlockEntity(pos) instanceof CapacitorBankBlockEntity capacitorBank) {
            return capacitorBank.getDisplayMode(horizontalFacing);
        }
        return DisplayMode.NONE;
    }

    public static void renderBottomHalf(PoseStack pose, SubmitNodeCollector submitNodeCollector, Direction facing, boolean isEnd) {
        TextureAtlasSprite texture = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(isEnd ? END_BAR : FULL_BAR);
        float inset = -0.001F;
        RenderUtil.submitFace(facing, pose, Sheets.cutoutBlockItemSheet(), submitNodeCollector, texture, 0, 0, inset, 1, 0.5f, 0xFFFFFFFF);
    }

    public static void renderTopHalf(PoseStack pose, SubmitNodeCollector submitNodeCollector, Direction facing, boolean isEnd) {
        TextureAtlasSprite texture = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(isEnd ? END_BAR : FULL_BAR);
        float inset = -0.001F;
        RenderUtil.submitFace(facing, pose, Sheets.cutoutBlockItemSheet(), submitNodeCollector, texture, 0, 0.5f, inset, 1, 0.5f, 0xFFFFFFFF);
    }

    public static void renderEnergy(PoseStack pose, SubmitNodeCollector submitNodeCollector, Direction facing, int pixels, boolean isBottomEnd) {
        TextureAtlasSprite texture = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(ENERGY_BAR);
        float inset = -0.002F;
        RenderUtil.submitFace(facing, pose, Sheets.cutoutBlockItemSheet(), submitNodeCollector, texture, 0, isBottomEnd ? 3/16f : 0, inset, 1, Math.min(pixels, isBottomEnd ? 13 : 16)/16f, 0xFFFFFFFF);
    }

    public static void renderTexture(PoseStack pose, SubmitNodeCollector submitNodeCollector, Direction facing, Identifier rl, int light) {
        TextureAtlasSprite texture = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(rl);
        float inset = -0.001F;
        RenderUtil.submitFace(facing, pose, Sheets.cutoutBlockItemSheet(), submitNodeCollector, texture, 0, 0, inset, 1, 1, 0xFFFFFFFF, light);
    }

    @Override
    public int getViewDistance() {
        return 92;
    }

    @Override
    public AABB getRenderBoundingBox(CapacitorBankBlockEntity blockEntity) {
        var pos = blockEntity.getBlockPos();
        return AABB.ofSize(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 32, 32, 32);
    }

    //TODO test performance, potentially try to cache
    private static Size findSize(CapacitorBankRenderState capacitorBank, Direction facing) {
        int x0 = 0;
        int y0 = 0;
        int x1 = 0;
        int y1 = 0;
        //expand right
        for (int i = 1; i < 16; i++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(i, 0)) != DisplayMode.IO) {
                break;
            }

            x1 = i;
        }
        //expand left
        for (int i = 1; i < 16 - x1; i++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(-i, 0)) != DisplayMode.IO) {
                break;
            }

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
                    if (checked > 0) {
                        return new Size(0,0,0,0);
                    }

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
                    if (checked > 0) {
                        return new Size(0,0,0,0);
                    }

                    break upSearch;
                }
            }
            y0 = -i;

        }

        return validateSize(new Size(x0, y0, x1, y1), capacitorBank, facing);
    }

    private static Size validateSize(Size size, CapacitorBankRenderState capacitorBank, Direction facing) {
        for (int x = size.x0; x <= size.x1; x++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(x, size.y0-1)) == DisplayMode.IO) {
                return new Size(0,0, 0, 0);
            }

            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(x, size.y1+1)) == DisplayMode.IO) {
                return new Size(0,0, 0, 0);
            }
        }

        for (int y = size.y0; y <= size.y1; y++) {
            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(size.x0-1, y)) == DisplayMode.IO) {
                return new Size(0,0, 0, 0);
            }

            if (getDisplayModeRelative(capacitorBank, facing, new Vector2i(size.x1+1, y)) == DisplayMode.IO) {
                return new Size(0,0, 0, 0);
            }
        }

        return size;
    }

    private record Size(int x0, int y0, int x1, int y1) {
        Identifier getTexture() {
            boolean isLeft = x0 == 0;
            boolean isRight = x1 == 0;
            boolean isSmallX = isLeft && isRight;
            boolean isUp = y0 == 0;
            boolean isDown = y1 == 0;
            boolean isSmallY = isUp && isDown;

            if (isSmallX && isSmallY) {
                return IO_1X1;
            }

            if (isSmallY) {
                if (isLeft) {
                    return EnderIO.id("block/capacitor_additionals/small_r");
                }

                if (isRight) {
                    return EnderIO.id("block/capacitor_additionals/small_l");
                }

                return EnderIO.id("block/capacitor_additionals/small_lr");
            }

            if (isSmallX) {
                if (isUp) {
                    return EnderIO.id("block/capacitor_additionals/small_u");
                }

                if (isDown) {
                    return EnderIO.id("block/capacitor_additionals/small_d");
                }

                return EnderIO.id("block/capacitor_additionals/small_ud");
            }

            if (isUp) {
                if (isLeft) {
                    return EnderIO.id("block/capacitor_additionals/corner_tr");
                }

                if (isRight) {
                    return EnderIO.id("block/capacitor_additionals/corner_tl");
                }
                
                return EnderIO.id("block/capacitor_additionals/side_t");
            }

            if (isDown) {
                if (isLeft) {
                    return EnderIO.id("block/capacitor_additionals/corner_br");
                }

                if (isRight) {
                    return EnderIO.id("block/capacitor_additionals/corner_bl");
                }

                return EnderIO.id("block/capacitor_additionals/side_b");
            }

            if (isLeft) {
                return EnderIO.id("block/capacitor_additionals/side_r");
            }

            if (isRight) {
                return EnderIO.id("block/capacitor_additionals/side_l");
            }

            return IO_FULL;
        }
    }
}
