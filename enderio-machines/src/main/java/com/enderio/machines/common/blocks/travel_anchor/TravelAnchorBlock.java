package com.enderio.machines.common.blocks.travel_anchor;

import com.enderio.base.api.travel.TravelTargetApi;
import com.enderio.machines.common.blocks.base.block.MachineBlock;
import com.enderio.machines.common.init.MachineBlockEntities;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TravelAnchorBlock<T extends TravelAnchorBlockEntity> extends MachineBlock<T> {
    public TravelAnchorBlock(Supplier<BlockEntityType<? extends T>> blockEntityType, Properties props) {
        super(blockEntityType, props);
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
            TravelTargetApi.INSTANCE.removeAt(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
