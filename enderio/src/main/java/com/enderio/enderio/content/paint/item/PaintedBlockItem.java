package com.enderio.enderio.content.paint.item;

import com.enderio.enderio.content.paint.PaintUtils;
import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PaintedBlockItem extends BlockItem {

    public PaintedBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player player) {
        return PaintUtils.getPlaceSound(state, level, pos, player, PaintedBlockItem.class)
            .orElseGet(() -> super.getPlaceSound(state, level, pos, player));
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        boolean result = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);

        var paintData = pStack.get(EIODataComponents.BLOCK_PAINT);

        if (paintData == null) {
            // TODO: Log error?
            return true;
        }

        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof PaintedBlockEntity singlePaintedBlockEntity) {
            singlePaintedBlockEntity.setPrimaryPaint(paintData.paint());
        }

        return result;
    }
}
