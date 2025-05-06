package com.enderio.machines.common.blocks.enderface;

import com.enderio.base.api.travel.TravelTargetApi;
import com.enderio.base.common.block.EIOEntityBlock;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EnderfaceBlock extends EIOEntityBlock<EnderfaceBlockEntity> {
    private static final MapCodec<EnderfaceBlock> CODEC = simpleCodec(EnderfaceBlock::new);

    public EnderfaceBlock(Properties properties) {
        super(MachineBlockEntities.ENDERFACE::get, properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof EnderfaceBlockEntity) {
            TravelTargetApi.INSTANCE.removeAt(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
