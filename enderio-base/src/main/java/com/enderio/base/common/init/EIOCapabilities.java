package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.filter.EntityFilter;
import com.enderio.base.api.filter.FilterMenuProvider;
import com.enderio.base.api.filter.FluidFilter;
import com.enderio.base.api.filter.ItemFilter;
import com.enderio.base.api.soul.SingleSoulStorageItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class EIOCapabilities {

    public static final class SideConfig {
        public static final BlockCapability<com.enderio.base.api.capability.SideConfig, Direction> BLOCK = BlockCapability
                .createSided(EnderIO.loc("side_config"), com.enderio.base.api.capability.SideConfig.class);
    }

    public static final ItemCapability<FilterMenuProvider, Void> FILTER_MENU_PROVIDER = ItemCapability
            .createVoid(EnderIO.loc("filter_menu_provider"), FilterMenuProvider.class);

    public static final ItemCapability<ItemFilter, Void> ITEM_FILTER = ItemCapability
            .createVoid(EnderIO.loc("item_filter"), ItemFilter.class);

    public static final ItemCapability<FluidFilter, Void> FLUID_FILTER = ItemCapability
            .createVoid(EnderIO.loc("fluid_filter"), FluidFilter.class);

    public static final ItemCapability<EntityFilter, Void> ENTITY_FILTER = ItemCapability
            .createVoid(EnderIO.loc("entity_filter"), EntityFilter.class);

    // TODO: Maybe we should put the I prefix back for parity with the rest of mods?
    public static final class SingleSoulStorage {
        public static final ItemCapability<com.enderio.base.api.soul.SingleSoulStorage, Void> ITEM = ItemCapability
                .createVoid(EnderIO.loc("soul_storage"), com.enderio.base.api.soul.SingleSoulStorage.class);

        public static final ICapabilityProvider<ItemStack, Void, com.enderio.base.api.soul.SingleSoulStorage> BASIC_ITEM_PROVIDER = (
                stack, ctx) -> new SingleSoulStorageItemStack(EIODataComponents.STORED_ENTITY, stack);
    }
}
