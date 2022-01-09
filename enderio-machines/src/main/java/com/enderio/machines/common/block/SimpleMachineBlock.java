package com.enderio.machines.common.block;

import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.entity.FallingMachineEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class SimpleMachineBlock extends ProgressMachineBlock implements Fallable {

    public SimpleMachineBlock(Properties properties, BlockEntityEntry<? extends MachineBlockEntity> blockEntityType) {
        super(properties, blockEntityType);
    }

    // region Falling Logic, copied from FallingBlock

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        level.scheduleTick(pos, this, 2);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos,
        BlockPos neighborPos) {
        level.scheduleTick(currentPos, this, 2);
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            FallingMachineEntity fallingblockentity = new FallingMachineEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getBlockState(pos));

            // Save NBT
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MachineBlockEntity) {
                fallingblockentity.blockData = be.saveWithoutMetadata();
            }

            level.addFreshEntity(fallingblockentity);
        }
    }

    // endregion
}
