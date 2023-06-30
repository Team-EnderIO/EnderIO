package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class SilentPressurePlateBlock extends PressurePlateBlock {

    public SilentPressurePlateBlock(PressurePlateBlock wrapped) {
        super(wrapped.sensitivity, Properties.copy(wrapped), EIOBlockSetType.SILENT);
    }

}
