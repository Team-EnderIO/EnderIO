package com.enderio.base.common.block;

import net.minecraft.world.level.block.PressurePlateBlock;

public class SilentPressurePlateBlock extends PressurePlateBlock {

    public SilentPressurePlateBlock(PressurePlateBlock wrapped) {
        super(EIOBlockSetType.SILENT, Properties.ofFullCopy(wrapped));
    }

}
