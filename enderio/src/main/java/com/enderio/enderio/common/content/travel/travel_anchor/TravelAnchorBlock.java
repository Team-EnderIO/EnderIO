package com.enderio.enderio.common.content.travel.travel_anchor;

import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.common.foundation.block.MachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class TravelAnchorBlock<T extends TravelAnchorBlockEntity> extends MachineBlock<T> {
    public TravelAnchorBlock(Supplier<BlockEntityType<? extends T>> blockEntityType, Properties props) {
        super(blockEntityType, props);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof TravelAnchorBlockEntity anchorBlock) {
            TravelTargetApi.INSTANCE.removeAt(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
