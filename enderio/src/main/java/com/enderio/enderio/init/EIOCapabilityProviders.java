package com.enderio.enderio.init;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.content.cold_fire.ColdFireIgniter;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilterItem;
import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import com.enderio.enderio.content.filters.item.general.EnderItemFilterItem;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import com.enderio.enderio.content.filters.soul.EnderSoulFilterItem;
import com.enderio.enderio.content.tools.LevitationStaffItem;
import com.enderio.enderio.content.tools.PoweredToggledItem;
import com.enderio.enderio.content.travel.TravelStaffItem;
import com.enderio.enderio.foundation.soul.SoulCapabilityProviders;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class EIOCapabilityProviders {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void markProxyableCapabilities(RegisterCapabilitiesEvent event) {
        // TODO: Review these
        event.setProxyable(EnderIOCapabilities.SIDE_CONFIG);
        event.setProxyable(EnderIOCapabilities.SOUL_BINDABLE_BLOCK);
        event.setProxyable(EnderIOCapabilities.SOUL_HANDLER_BLOCK);
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        registerConduitCapabilities(event);

        // Register item energy handlers
        event.registerItem(Capabilities.EnergyStorage.ITEM, PoweredToggledItem.ENERGY_STORAGE_PROVIDER,
            EIOItems.LEVITATION_STAFF, EIOItems.ELECTROMAGNET);
        event.registerItem(Capabilities.EnergyStorage.ITEM, TravelStaffItem.ENERGY_STORAGE_PROVIDER, EIOItems.TRAVEL_STAFF);

        // Register item fluid handlers
        event.registerItem(Capabilities.FluidHandler.ITEM, LevitationStaffItem.FLUID_HANDLER_PROVIDER, EIOItems.LEVITATION_STAFF);
        event.registerItem(Capabilities.FluidHandler.ITEM, ColdFireIgniter.FLUID_HANDLER_PROVIDER, EIOItems.COLD_FIRE_IGNITER);

        // Filter menu providers
        event.registerItem(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER, EIOItems.BASIC_ITEM_FILTER,
            EIOItems.ADVANCED_ITEM_FILTER, EIOItems.BIG_ITEM_FILTER, EIOItems.BIG_ADVANCED_ITEM_FILTER, EIOItems.BASIC_ITEM_FILTER, EIOItems.BASIC_SOUL_FILTER);

        // Filter providers
        event.registerItem(EnderIOCapabilities.ITEM_FILTER, EnderItemFilterItem.ITEM_FILTER_PROVIDER, EIOItems.BASIC_ITEM_FILTER, EIOItems.ADVANCED_ITEM_FILTER,
            EIOItems.BIG_ITEM_FILTER, EIOItems.BIG_ADVANCED_ITEM_FILTER);

        event.registerItem(EnderIOCapabilities.FLUID_FILTER, EnderFluidFilterItem.FLUID_FILTER_PROVIDER, EIOItems.BASIC_FLUID_FILTER);

        event.registerItem(EnderIOCapabilities.SOUL_FILTER, EnderSoulFilterItem.ENTITY_FILTER_PROVIDER, EIOItems.BASIC_SOUL_FILTER);

        // TODO: Testing the waters on doing capabilities by hand.
        // Register soul bindable items
        event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM,
            SoulCapabilityProviders.COMPONENT_SOUL_BINDABLE_PROVIDER,
            EIOItems.BROKEN_SPAWNER);

        // Register read-only soul bindable items
        // Soul vial uses a read-only ISoulBindable because the soul vial is a storage which can be used for binding, but is not directly bound to.
        event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM,
            SoulCapabilityProviders.READ_ONLY_COMPONENT_SOUL_BINDABLE_PROVIDER,
            EIOItems.SOUL_VIAL);

        // Register single soul handler items
        event.registerItem(EnderIOCapabilities.SOUL_HANDLER_ITEM,
            SoulCapabilityProviders.SINGLE_COMPONENT_SOUL_HANDLER_PROVIDER,
            EIOItems.SOUL_VIAL);
    }

    // region Conduits

    private static void registerConduitCapabilities(RegisterCapabilitiesEvent event) {
        EnderIORegistries.CONDUIT_TYPE.entrySet()
            .stream()
            .flatMap(e -> e.getValue().exposedCapabilities().stream())
            .forEach(e -> registerConduitCapability(event, e));
    }

    private static <TCap, TContext> void registerConduitCapability(RegisterCapabilitiesEvent event,
        BlockCapability<TCap, TContext> capability) {
        event.registerBlockEntity(capability, EIOBlockEntities.CONDUIT.get(),
            ConduitBundleBlockEntity.createCapabilityProvider(capability));
    }

    // endregion
}
