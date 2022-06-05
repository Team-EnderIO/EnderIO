package com.enderio.base.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class PaintUtils {
    public static Block getBlockFromRL(String rl) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(rl));
    }
}
