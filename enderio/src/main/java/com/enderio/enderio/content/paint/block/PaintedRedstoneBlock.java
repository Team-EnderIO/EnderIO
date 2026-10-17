package com.enderio.enderio.content.paint.block;

import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class PaintedRedstoneBlock extends PoweredBlock implements EntityBlock, PaintedBlock {

    public PaintedRedstoneBlock(Properties properties) {
        super(properties);
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
    public BlockState getAppearance(BlockState state, BlockAndLightGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof PaintedBlockEntity painted) {
            Optional<Block> paint = painted.getPrimaryPaint();

            if (paint.isPresent()) {
                return paint.get().defaultBlockState();
            }
        }

        return super.getAppearance(state, level, pos, side, queryState, queryPos);
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
