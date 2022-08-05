package com.enderio.conduits.common.blockentity;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ConduitBlockEntity extends EnderBlockEntity {

    //TODO: insertBlock and check if 0 is a viable ticksetting
    private final ConduitBundle bundle = new ConduitBundle(() -> {
        if (!level.isClientSide())
            level.scheduleTick(getBlockPos(),(Block)null, 0);
    });

    public ConduitBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        bundle.gatherDataSlots().forEach(this::addDataSlot);
    }
}
