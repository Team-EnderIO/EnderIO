package com.enderio.enderio.content.paint.block;

import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class PaintedWallBlock extends WallBlock implements EntityBlock, PaintedBlock {

    public PaintedWallBlock(Properties p_57964_) {
        super(p_57964_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SinglePaintedBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        // We ignore includeData because without this data the item won't work :P
        return getPaintedStack(level, pos, this);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        var paintData = itemStack.get(EIODataComponents.BLOCK_PAINT);
        if (!level.isClientSide() && paintData != null &&
            level.getBlockEntity(pos) instanceof SinglePaintedBlockEntity painted) {
            painted.setPrimaryPaint(paintData.paint());
        }
    }
}
