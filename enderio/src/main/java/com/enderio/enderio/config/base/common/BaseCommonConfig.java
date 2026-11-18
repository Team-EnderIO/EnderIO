package com.enderio.enderio.config.base.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BaseCommonConfig {
    public final BlocksConfig BLOCKS;
    public final EnergyConfig ENERGY;
    public final ItemsConfig ITEMS;
    public final InfinityConfig INFINITY;

    public BaseCommonConfig(ModConfigSpec.Builder builder) {
        BLOCKS = new BlocksConfig(builder);
        ENERGY = new EnergyConfig(builder);
        ITEMS = new ItemsConfig(builder);
        INFINITY = new InfinityConfig(builder);
    }
}
