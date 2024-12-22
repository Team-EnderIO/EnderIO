package com.enderio.machines.common.block;

import com.enderio.machines.common.blockentity.base.LegacyMachineBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * A block that can display its powered state when performing its action.
 */
public class LegacyProgressMachineBlock extends LegacyMachineBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LegacyProgressMachineBlock(RegiliteBlockEntity<? extends LegacyMachineBlockEntity> blockEntityType,
            Properties properties) {
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
