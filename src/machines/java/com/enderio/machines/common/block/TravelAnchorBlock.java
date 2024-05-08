package com.enderio.machines.common.block;

import com.enderio.api.travel.TravelTargetAPI;
import com.enderio.machines.common.blockentity.TravelAnchorBlockEntity;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TravelAnchorBlock extends MachineBlock {
    public TravelAnchorBlock(RegiliteBlockEntity<? extends MachineBlockEntity> blockEntityType, Properties props) {
        super(blockEntityType, props);
    }

    public TravelAnchorBlock(Properties props) {
        this(MachineBlockEntities.TRAVEL_ANCHOR, props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return MachineBlockEntities.TRAVEL_ANCHOR.create(pPos, pState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof TravelAnchorBlockEntity anchorBlock) {
            TravelTargetAPI.removeAt(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
