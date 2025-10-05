package com.enderio.enderio.content.machines.powered_spawner;

import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MindKillerBlockEntity extends BlockEntity {

    public MindKillerBlockEntity(BlockPos pos, BlockState blockState) {
        super(EIOBlockEntities.MIND_KILLER.get(), pos, blockState);
    }
}
