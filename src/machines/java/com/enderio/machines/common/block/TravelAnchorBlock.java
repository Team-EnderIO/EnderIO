package com.enderio.machines.common.block;

import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.machines.common.blockentity.TravelAnchorBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TravelAnchorBlock extends MachineBlock {
    public TravelAnchorBlock(Properties props) {
        super(props, MachineBlockEntities.TRAVEL_ANCHOR);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return MachineBlockEntities.TRAVEL_ANCHOR.create(pPos, pState);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof TravelAnchorBlockEntity anchorBlock) {
            TravelSavedData.getTravelData(level).removeTravelTargetAt(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
