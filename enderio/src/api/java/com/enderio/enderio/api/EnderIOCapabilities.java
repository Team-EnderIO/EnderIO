package com.enderio.enderio.api;

import com.enderio.enderio.api.capability.SideConfig;
import com.enderio.enderio.api.filter.FilterMenuProvider;
import com.enderio.enderio.api.filter.FluidFilter;
import com.enderio.enderio.api.filter.ItemFilter;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class EnderIOCapabilities {
    public static final BlockCapability<SideConfig, Direction> SIDE_CONFIG = BlockCapability
        .createSided(EnderIO.loc("side_config"), SideConfig.class);

    public static final ItemCapability<FilterMenuProvider, Void> FILTER_MENU_PROVIDER = ItemCapability
        .createVoid(EnderIO.loc("filter_menu_provider"), FilterMenuProvider.class);

    public static final ItemCapability<ItemFilter, Void> ITEM_FILTER = ItemCapability
        .createVoid(EnderIO.loc("item_filter"), ItemFilter.class);

    public static final ItemCapability<FluidFilter, Void> FLUID_FILTER = ItemCapability
        .createVoid(EnderIO.loc("fluid_filter"), FluidFilter.class);

    public static final ItemCapability<SoulFilter, Void> SOUL_FILTER = ItemCapability
        .createVoid(EnderIO.loc("soul_filter"), SoulFilter.class);

    public static final ItemCapability<SoulBindable, Void> SOUL_BINDABLE_ITEM = ItemCapability
        .createVoid(EnderIO.loc("soul_bindable"), SoulBindable.class);

    public static final BlockCapability<SoulBindable, Void> SOUL_BINDABLE_BLOCK = BlockCapability
        .createVoid(EnderIO.loc("soul_bindable"), SoulBindable.class);

    public static final ItemCapability<com.enderio.enderio.api.soul.storage.SoulHandler, Void> SOUL_HANDLER_ITEM = ItemCapability
        .createVoid(EnderIO.loc("soul_handler"), com.enderio.enderio.api.soul.storage.SoulHandler.class);

    public static final BlockCapability<SoulHandler, @Nullable Direction> SOUL_HANDLER_BLOCK = BlockCapability
        .createSided(EnderIO.loc("soul_handler"), SoulHandler.class);
}
