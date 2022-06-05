package com.enderio.base.common.blockentity;

import net.minecraft.world.level.block.Block;

public interface IPaintableBlockEntity {
    Block getPaint();

    default Block[] getPaints() {
        return new Block[] { getPaint() };
    }
}
