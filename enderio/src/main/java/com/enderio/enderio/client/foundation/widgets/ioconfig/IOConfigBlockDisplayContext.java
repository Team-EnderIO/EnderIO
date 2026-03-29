package com.enderio.enderio.client.foundation.widgets.ioconfig;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.core.BlockPos;

/**
 * Provides the level and position of the block for rendering in the IO Config widget.
 * Hopefully there'll be a solution added to NeoForge to handle this compatibly across mods.
 */
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
