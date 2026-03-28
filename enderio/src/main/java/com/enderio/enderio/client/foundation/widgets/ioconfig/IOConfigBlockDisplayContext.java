package com.enderio.enderio.client.foundation.widgets.ioconfig;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.core.BlockPos;

public class IOConfigBlockDisplayContext extends BlockDisplayContext {
    private final BlockAndTintGetter realLevel;
    private final BlockPos pos;

    public IOConfigBlockDisplayContext(BlockAndTintGetter realLevel, BlockPos pos) {
        this.realLevel = realLevel;
        this.pos = pos;
    }

    public BlockAndTintGetter realLevel() {
        return realLevel;
    }

    public BlockPos pos() {
        return pos;
    }
}
