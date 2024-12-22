package com.enderio.base.common.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class EIOEntityBlock<T extends EIOBlockEntity> extends BaseEntityBlock {

    private final Supplier<BlockEntityType<? extends T>> typeSupplier;

    protected EIOEntityBlock(Supplier<BlockEntityType<? extends T>> typeSupplier, Properties properties) {
        super(properties);
        this.typeSupplier = typeSupplier;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return typeSupplier.get().create(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, typeSupplier.get(), EIOBlockEntity::tick);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
            @Nullable Direction direction) {
        if (level.getBlockEntity(pos) instanceof EIOBlockEntity blockEntity) {
            return blockEntity.supportsRedstonePower();
        }

        return super.canConnectRedstone(state, level, pos, direction);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
            BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (level.getBlockEntity(pos) instanceof EIOBlockEntity blockEntity) {
            blockEntity.neighborChanged(neighborBlock, neighborPos);
        }
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        if (level.getBlockEntity(pos) instanceof EIOBlockEntity blockEntity) {
            blockEntity.neighborChanged(level.getBlockState(neighbor).getBlock(), neighbor);
        }
    }
}
