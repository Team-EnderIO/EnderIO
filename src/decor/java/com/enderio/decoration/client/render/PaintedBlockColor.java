package com.enderio.decoration.client.render;

import com.enderio.decoration.common.blockentity.IPaintableBlockEntity;
import com.enderio.decoration.common.util.PaintUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class PaintedBlockColor implements BlockColor, ItemColor {

    // TODO: Buggy on the sides of blocks. (Grass)
    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level != null && pos != null && tintIndex != 0) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof IPaintableBlockEntity paintedBlockEntity) {
                Block[] paints = paintedBlockEntity.getPaints();
                for (Block paint : paints) {
                    if (paint == null)
                        continue;
                    BlockState paintState = paint.defaultBlockState();
                    int color = Minecraft.getInstance().getBlockColors().getColor(paintState, level, pos, tintIndex);
                    if (color != -1)
                        return color;
                }
            }
        }
        return 0xFFFFFF;
    }

    @Override
    public int getColor(ItemStack itemStack, int tintIndex) {
        if (itemStack.getTag() != null && itemStack.getTag().contains("BlockEntityTag")) {
            CompoundTag blockEntityTag = itemStack.getTag().getCompound("BlockEntityTag");
            if (blockEntityTag.contains("paint")) {
                Block paint = PaintUtils.getBlockFromRL(blockEntityTag.getString("paint"));
                if (paint == null)
                    return 0;
                return Minecraft.getInstance().getItemColors().getColor(paint.asItem().getDefaultInstance(), tintIndex);
            }
        }
        return 0;
    }
}