package com.enderio.api.paint;

import net.minecraft.world.level.block.Block;

public interface IPaintableBlockEntity {
    Block getPaint();

    default Block[] getPaints() {
        return new Block[] { getPaint() };
    }
}
