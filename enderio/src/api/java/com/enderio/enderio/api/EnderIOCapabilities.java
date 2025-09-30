package com.enderio.enderio.api;

import com.enderio.enderio.api.conduits.facade.ConduitFacadeProvider;
import com.enderio.enderio.api.filter.FilterMenuProvider;
import com.enderio.enderio.api.filter.FluidFilter;
import com.enderio.enderio.api.filter.ItemFilter;
import com.enderio.enderio.api.filter.RedstoneInputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.api.io.SideConfig;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class EnderIOCapabilities {
    public static final BlockCapability<SideConfig, Direction> SIDE_CONFIG = BlockCapability
        .createSided(EnderIOAPI.loc("side_config"), SideConfig.class);

    public static final ItemCapability<FilterMenuProvider, Void> FILTER_MENU_PROVIDER = ItemCapability
        .createVoid(EnderIOAPI.loc("filter_menu_provider"), FilterMenuProvider.class);

    public static final ItemCapability<ItemFilter, Void> ITEM_FILTER = ItemCapability
        .createVoid(EnderIOAPI.loc("item_filter"), ItemFilter.class);

    public static final ItemCapability<FluidFilter, Void> FLUID_FILTER = ItemCapability
        .createVoid(EnderIOAPI.loc("fluid_filter"), FluidFilter.class);

    public static final ItemCapability<SoulFilter, Void> SOUL_FILTER = ItemCapability
        .createVoid(EnderIOAPI.loc("soul_filter"), SoulFilter.class);

    public static final ItemCapability<SoulBindable, Void> SOUL_BINDABLE_ITEM = ItemCapability
        .createVoid(EnderIOAPI.loc("soul_bindable"), SoulBindable.class);

    public static final BlockCapability<SoulBindable, Void> SOUL_BINDABLE_BLOCK = BlockCapability
        .createVoid(EnderIOAPI.loc("soul_bindable"), SoulBindable.class);

    public static final ItemCapability<com.enderio.enderio.api.soul.storage.SoulHandler, Void> SOUL_HANDLER_ITEM = ItemCapability
        .createVoid(EnderIOAPI.loc("soul_handler"), com.enderio.enderio.api.soul.storage.SoulHandler.class);

    public static final BlockCapability<SoulHandler, @Nullable Direction> SOUL_HANDLER_BLOCK = BlockCapability
        .createSided(EnderIOAPI.loc("soul_handler"), SoulHandler.class);

    public static final ItemCapability<ConduitFacadeProvider, Void> CONDUIT_FACADE_PROVIDER = ItemCapability
        .createVoid(EnderIOAPI.loc("conduit_facade_provider"), ConduitFacadeProvider.class);

    public static final ItemCapability<RedstoneOutputFilter, Void> REDSTONE_INSERT_FILTER = ItemCapability
        .createVoid(EnderIOAPI.loc("redstone_insert_filter"), RedstoneOutputFilter.class);

    public static final ItemCapability<RedstoneInputFilter, Void> REDSTONE_EXTRACT_FILTER = ItemCapability
        .createVoid(EnderIOAPI.loc("redstone_extract_filter"), RedstoneInputFilter.class);
}
