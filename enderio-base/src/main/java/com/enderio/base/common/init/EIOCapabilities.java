package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.filter.ResourceFilter;
import com.enderio.base.api.new_filter.FilterMenuProvider;
import com.enderio.base.api.new_filter.FluidStackFilter;
import com.enderio.base.api.new_filter.ItemStackFilter;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class EIOCapabilities {

    public static final class SideConfig {
        public static final BlockCapability<com.enderio.base.api.capability.SideConfig, Direction> BLOCK = BlockCapability
                .createSided(EnderIO.loc("side_config"), com.enderio.base.api.capability.SideConfig.class);
    }

    public static final class Filter {
        public static final ItemCapability<ResourceFilter, Void> ITEM = ItemCapability
                .createVoid(EnderIO.loc("item_filter"), ResourceFilter.class);
    }

    public static final ItemCapability<FilterMenuProvider, Void> FILTER_MENU_PROVIDER = ItemCapability
            .createVoid(EnderIO.loc("filter_menu_provider"), FilterMenuProvider.class);

    public static final ItemCapability<ItemStackFilter, Void> ITEM_STACK_FILTER = ItemCapability
            .createVoid(EnderIO.loc("item_stack_filter"), ItemStackFilter.class);

    public static final ItemCapability<FluidStackFilter, Void> FLUID_STACK_FILTER = ItemCapability
            .createVoid(EnderIO.loc("fluid_stack_filter"), FluidStackFilter.class);
}
