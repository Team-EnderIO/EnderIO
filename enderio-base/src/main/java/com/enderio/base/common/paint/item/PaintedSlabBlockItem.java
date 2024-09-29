package com.enderio.base.common.paint.item;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.blockentity.PaintedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public class PaintedSlabBlockItem extends PaintedBlockItem {

    public PaintedSlabBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        boolean result = updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);

        var paintData = pStack.get(EIODataComponents.BLOCK_PAINT);

        if (paintData == null) {
            // TODO: Log error
            return true;
        }

        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof PaintedBlockEntity paintedBlockEntity) {
            if (pState.getValue(SlabBlock.TYPE) != SlabType.BOTTOM) {
                paintedBlockEntity.setSecondaryPaint(paintData.paint());
            } else if (pState.getValue(SlabBlock.TYPE) != SlabType.TOP) {
                paintedBlockEntity.setPrimaryPaint(paintData.paint());
            }
        }

        return result;
    }
}
