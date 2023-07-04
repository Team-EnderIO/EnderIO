package com.enderio.base.common.block;

import net.minecraft.world.level.block.PressurePlateBlock;

public class SilentPressurePlateBlock extends PressurePlateBlock {

    public SilentPressurePlateBlock(PressurePlateBlock wrapped) {
        super(wrapped.sensitivity, Properties.copy(wrapped), EIOBlockSetType.SILENT);
    }

}
