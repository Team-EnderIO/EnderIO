package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MindKillerBlockEntity extends BlockEntity {

    public MindKillerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MachineBlockEntities.MIND_KILLER.get(), pos, blockState);
    }
}
