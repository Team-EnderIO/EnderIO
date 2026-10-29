package com.enderio.enderio.api;

import com.enderio.enderio.api.capacitor.ICapacitorExtension;
import com.enderio.enderio.api.conduits.facade.ConduitFacadeProvider;
import com.enderio.enderio.api.filter.FilterMenuProvider;
import com.enderio.enderio.api.filter.FluidFilter;
import com.enderio.enderio.api.filter.ItemFilter;
import com.enderio.enderio.api.filter.RedstoneInputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.api.io.SideConfig;
import com.enderio.enderio.api.soul.binding.BindableSoulSource;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.SoulResource;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

public class EnderIOCapabilities {
    public static final BlockCapability<SideConfig, Direction> SIDE_CONFIG = BlockCapability
        .createSided(EnderIOAPI.rl("side_config"), SideConfig.class);

    public static final ItemCapability<ICapacitorExtension, Void> CAPACITOR_EXTENSION = ItemCapability
        .createVoid(EnderIOAPI.rl("capacitor_extension"), ICapacitorExtension.class);

    public static final ItemCapability<FilterMenuProvider, ItemAccess> FILTER_MENU_PROVIDER = ItemCapability
        .create(EnderIOAPI.rl("filter_menu_provider"), FilterMenuProvider.class, ItemAccess.class);

    public static final ItemCapability<ItemFilter, Void> ITEM_FILTER = ItemCapability
        .createVoid(EnderIOAPI.rl("item_filter"), ItemFilter.class);

    public static final ItemCapability<FluidFilter, Void> FLUID_FILTER = ItemCapability
        .createVoid(EnderIOAPI.rl("fluid_filter"), FluidFilter.class);

    public static final ItemCapability<SoulFilter, Void> SOUL_FILTER = ItemCapability
        .createVoid(EnderIOAPI.rl("soul_filter"), SoulFilter.class);

    public static final ItemCapability<SoulBindable, ItemAccess> SOUL_BINDABLE_ITEM = ItemCapability
        .create(EnderIOAPI.rl("soul_bindable"), SoulBindable.class, ItemAccess.class);

    public static final BlockCapability<SoulBindable, Void> SOUL_BINDABLE_BLOCK = BlockCapability
        .createVoid(EnderIOAPI.rl("soul_bindable"), SoulBindable.class);

    public static final ItemCapability<BindableSoulSource, ItemAccess> BINDABLE_SOUL_SOURCE = ItemCapability
        .create(EnderIOAPI.rl("bindable_soul_source"), BindableSoulSource.class, ItemAccess.class);

    public static class Soul {
        public static final ItemCapability<ResourceHandler<SoulResource>, ItemAccess> ITEM = ItemCapability
            .create(EnderIOAPI.rl("soul_resource"), ResourceHandler.asClass(), ItemAccess.class);

        public static final BlockCapability<ResourceHandler<SoulResource>, @Nullable Direction> BLOCK = BlockCapability
            .createSided(EnderIOAPI.rl("soul_handler"), ResourceHandler.asClass());
    }

    public static final ItemCapability<ConduitFacadeProvider, Void> CONDUIT_FACADE_PROVIDER = ItemCapability
        .createVoid(EnderIOAPI.rl("conduit_facade_provider"), ConduitFacadeProvider.class);

    public static final ItemCapability<RedstoneOutputFilter, Void> REDSTONE_INSERT_FILTER = ItemCapability
        .createVoid(EnderIOAPI.rl("redstone_insert_filter"), RedstoneOutputFilter.class);

    public static final ItemCapability<RedstoneInputFilter, Void> REDSTONE_EXTRACT_FILTER = ItemCapability
        .createVoid(EnderIOAPI.rl("redstone_extract_filter"), RedstoneInputFilter.class);
}
