package com.enderio.base.common.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class BaseCommonConfig {
    public final BlocksConfig BLOCKS;
    public final EnchantmentsConfig ENCHANTMENTS;
    public final ItemsConfig ITEMS;
    public final InfinityConfig INFINITY;

    public BaseCommonConfig(ForgeConfigSpec.Builder builder) {
        BLOCKS = new BlocksConfig(builder);
        ENCHANTMENTS = new EnchantmentsConfig(builder);
        ITEMS = new ItemsConfig(builder);
        INFINITY = new InfinityConfig(builder);
    }
}
