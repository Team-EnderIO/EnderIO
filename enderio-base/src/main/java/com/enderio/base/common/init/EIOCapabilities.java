package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.api.filter.ResourceFilter;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class EIOCapabilities {

    public static final class SideConfig {
        public static final BlockCapability<com.enderio.base.api.capability.SideConfig, Direction> BLOCK =
            BlockCapability.createSided(
                EnderIOBase.loc("side_config"),
                com.enderio.base.api.capability.SideConfig.class);
    }

    public static final class Filter {
        public static final ItemCapability<ResourceFilter, Void> ITEM =
            ItemCapability.createVoid(
                EnderIOBase.loc("item_filter"),
                ResourceFilter.class);
    }
}
