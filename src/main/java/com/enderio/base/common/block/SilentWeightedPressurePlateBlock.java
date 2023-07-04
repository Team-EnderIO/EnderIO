package com.enderio.base.common.block;

import net.minecraft.world.level.block.WeightedPressurePlateBlock;

public class SilentWeightedPressurePlateBlock extends WeightedPressurePlateBlock {

    public SilentWeightedPressurePlateBlock(WeightedPressurePlateBlock from) {
        super(from.maxWeight, Properties.copy(from), EIOBlockSetType.SILENT);
    }

}
