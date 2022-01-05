package com.enderio.machines.common.block;

import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ProgressMachineBlock extends MachineBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ProgressMachineBlock(Properties p_49795_, BlockEntityEntry<? extends MachineBlockEntity> blockEntityType) {
        super(p_49795_, blockEntityType);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }
}
