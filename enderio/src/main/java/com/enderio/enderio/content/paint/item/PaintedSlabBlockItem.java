package com.enderio.enderio.content.paint.item;

import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jspecify.annotations.Nullable;

public class PaintedSlabBlockItem extends PaintedBlockItem {

    public PaintedSlabBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        boolean result = updateCustomBlockEntityTag(level, player, pos, stack);

        var paintData = stack.get(EIODataComponents.BLOCK_PAINT);

        if (paintData == null) {
            // TODO: Log error
            return true;
        }

        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof PaintedBlockEntity paintedBlockEntity) {
            if (state.getValue(SlabBlock.TYPE) != SlabType.BOTTOM) {
                paintedBlockEntity.setSecondaryPaint(paintData.paint());
            } else if (state.getValue(SlabBlock.TYPE) != SlabType.TOP) {
                paintedBlockEntity.setPrimaryPaint(paintData.paint());
            }
        }

        return result;
    }
}
