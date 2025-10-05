package com.enderio.enderio.content.misc_blocks;

import com.enderio.enderio.foundation.block.EIOBlockSetType;
import net.minecraft.world.level.block.PressurePlateBlock;

public class SilentPressurePlateBlock extends PressurePlateBlock {

    public SilentPressurePlateBlock(PressurePlateBlock wrapped) {
        super(EIOBlockSetType.SILENT, Properties.ofFullCopy(wrapped));
    }

}
