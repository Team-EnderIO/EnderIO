package com.enderio.enderio.foundation.block;

import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.function.Supplier;

/**
 * A block that can display its powered state when performing its action.
 */
public class ProgressMachineBlock<T extends MachineBlockEntity> extends MachineBlock<T> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ProgressMachineBlock(Supplier<BlockEntityType<? extends T>> blockEntityType, Properties properties) {
        super(blockEntityType, properties);
        this.registerDefaultState(
                this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }
}
