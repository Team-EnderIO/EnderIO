package com.enderio.enderio.content.travel.travel_anchor;

import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.foundation.block.MachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class TravelAnchorBlock<T extends TravelAnchorBlockEntity> extends MachineBlock<T> {
    public TravelAnchorBlock(Supplier<BlockEntityType<? extends T>> blockEntityType, Properties props) {
        super(blockEntityType, props);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof TravelAnchorBlockEntity anchorBlock) {
            TravelTargetApi.INSTANCE.removeAt(anchorBlock.getLevel(), pos);
        }

        super.destroy(level, pos, state);
    }
}
