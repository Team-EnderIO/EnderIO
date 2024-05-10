package com.enderio.base.client.renderer;

import com.enderio.base.common.paint.block.PaintedBlock;
import com.enderio.base.common.paint.block.PaintedSlabBlock;
import com.enderio.base.common.paint.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.paint.blockentity.PaintedBlockEntity;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PaintedBlockColor implements BlockColor, ItemColor {

    public static PaintedBlockColor INSTANCE = new PaintedBlockColor();

    // TODO: Buggy on the sides of blocks. (Grass)
    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level != null && pos != null) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof PaintedBlockEntity paintedBlockEntity) {

                Optional<Block> paint = paintedBlockEntity.getPrimaryPaint();
                if (state.getBlock() instanceof PaintedSlabBlock && tintIndex < 0) {
                    paint = paintedBlockEntity.getSecondaryPaint();
                }

                tintIndex = unmoveTintIndex(tintIndex);

                BlockState paintState = paint.orElse(PaintedBlock.DEFAULT_PAINT).defaultBlockState();
                int color = Minecraft.getInstance().getBlockColors().getColor(paintState, level, pos, tintIndex);
                if (color != -1) {
                    return color;
                }
            }
        }

        return 0xFFFFFF;
    }

    @Override
    public int getColor(ItemStack itemStack, int tintIndex) {
        var paintData = itemStack.get(EIODataComponents.BLOCK_PAINT);
        if (paintData != null) {
            var block = paintData.paint();
            return Minecraft.getInstance().getItemColors().getColor(block.asItem().getDefaultInstance(), tintIndex);
        }

        return 0;
    }

    //Lets assume that no one uses negative tint indices, so that we can move bottomslab tint indices into the negative half
    public static int moveTintIndex(int original) {
        return -original - 2;
    }
    public static int unmoveTintIndex(int original) {
        if (original > 0) {
            return original;
        } else {
            return -original + 2;
        }
    }
}
