package com.enderio.enderio.content.travel.travel_anchor;

import com.enderio.enderio.foundation.block.MachineBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class TravelAnchorBlock<T extends TravelAnchorBlockEntity> extends MachineBlock<T> {
    public TravelAnchorBlock(Supplier<BlockEntityType<? extends T>> blockEntityType, Properties props) {
        super(blockEntityType, props);
    }
}
