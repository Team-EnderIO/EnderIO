package com.enderio.enderio.content.misc_blocks;

import com.enderio.enderio.foundation.block.EIOBlockSetType;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;

public class SilentWeightedPressurePlateBlock extends WeightedPressurePlateBlock {

    public SilentWeightedPressurePlateBlock(int maxWeight, Properties properties) {
        super(maxWeight, EIOBlockSetType.SILENT, properties);
    }

}
