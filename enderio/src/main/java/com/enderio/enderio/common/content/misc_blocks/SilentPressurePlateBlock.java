package com.enderio.enderio.common.content.misc_blocks;

import com.enderio.enderio.common.foundation.block.EIOBlockSetType;
import net.minecraft.world.level.block.PressurePlateBlock;

public class SilentPressurePlateBlock extends PressurePlateBlock {

    public SilentPressurePlateBlock(PressurePlateBlock wrapped) {
        super(EIOBlockSetType.SILENT, Properties.ofFullCopy(wrapped));
    }

}
