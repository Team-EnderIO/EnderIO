package com.enderio.enderio.init;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.content.cold_fire.ColdFireIgniter;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.conduits.facades.ComponentBackedConduitFacadeProvider;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilterItem;
import com.enderio.enderio.content.filters.item.general.EnderItemFilterItem;
import com.enderio.enderio.content.filters.redstone.RedstoneANDFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneCountFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneNANDFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneNORFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneNOTFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneORFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneSensorFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTLatchFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneXNORFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneXORFilter;
import com.enderio.enderio.content.filters.soul.EnderSoulFilterItem;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankItem;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockItem;
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
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        registerConduitCapabilities(event);

        // Register item energy handlers
        event.registerItem(Capabilities.Energy.ITEM, PoweredToggledItem.ENERGY_STORAGE_PROVIDER,
            EIOItems.LEVITATION_STAFF, EIOItems.ELECTROMAGNET);
        event.registerItem(Capabilities.Energy.ITEM, TravelStaffItem.ENERGY_STORAGE_PROVIDER, EIOItems.TRAVEL_STAFF);
        // Capacitor Banks
        for (var capacitorBankItem : EIOBlocks.CAPACITOR_BANK_ITEMS.values()) {
            event.registerItem(Capabilities.Energy.ITEM, CapacitorBankItem.ENERGY_STORAGE_PROVIDER, capacitorBankItem);
        }

        // Register item fluid handlers
        event.registerItem(Capabilities.Fluid.ITEM, LevitationStaffItem.FLUID_HANDLER_PROVIDER, EIOItems.LEVITATION_STAFF);
        event.registerItem(Capabilities.Fluid.ITEM, ColdFireIgniter.FLUID_HANDLER_PROVIDER, EIOItems.COLD_FIRE_IGNITER);
        event.registerItem(Capabilities.Fluid.ITEM, FluidTankBlockItem.FLUID_HANDLER_PROVIDER,
            EIOBlocks.FLUID_TANK_ITEM, EIOBlocks.PRESSURIZED_FLUID_TANK_ITEM);

        // region Filters

        // Filter menu providers
        event.registerItem(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER, EIOItems.BASIC_ITEM_FILTER,
            EIOItems.ADVANCED_ITEM_FILTER, EIOItems.BIG_ITEM_FILTER, EIOItems.BIG_ADVANCED_ITEM_FILTER, EIOItems.BASIC_ITEM_FILTER, EIOItems.BASIC_SOUL_FILTER);

        // Filter providers
        event.registerItem(EnderIOCapabilities.ITEM_FILTER, EnderItemFilterItem.ITEM_FILTER_PROVIDER, EIOItems.BASIC_ITEM_FILTER, EIOItems.ADVANCED_ITEM_FILTER,
            EIOItems.BIG_ITEM_FILTER, EIOItems.BIG_ADVANCED_ITEM_FILTER);

        event.registerItem(EnderIOCapabilities.FLUID_FILTER, EnderFluidFilterItem.FLUID_FILTER_PROVIDER, EIOItems.BASIC_FLUID_FILTER);

        event.registerItem(EnderIOCapabilities.SOUL_FILTER, EnderSoulFilterItem.ENTITY_FILTER_PROVIDER, EIOItems.BASIC_SOUL_FILTER);

        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> RedstoneNOTFilter.INSTANCE, EIOItems.NOT_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER, (s, c) -> RedstoneNOTFilter.INSTANCE, EIOItems.NOT_FILTER);

        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneORFilter(s), EIOItems.OR_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneANDFilter(s), EIOItems.AND_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneNORFilter(s), EIOItems.NOR_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneNANDFilter(s), EIOItems.NAND_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneXORFilter(s), EIOItems.XOR_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneXNORFilter(s), EIOItems.XNOR_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneTLatchFilter(s), EIOItems.TLATCH_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_INSERT_FILTER, (s, c) -> new RedstoneCountFilter(s), EIOItems.COUNT_FILTER);

        event.registerItem(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER, (s, c) -> RedstoneSensorFilter.INSTANCE, EIOItems.SENSOR_FILTER);
        event.registerItem(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER, (s, c) -> new RedstoneTimerFilter(s), EIOItems.TIMER_FILTER);

        // endregion

        // TODO: Testing the waters on doing capabilities by hand.
        // Register soul bindable items
        event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM,
            SoulCapabilityProviders.COMPONENT_SOUL_BINDABLE_PROVIDER,
            EIOItems.BROKEN_SPAWNER,
            EIOBlocks.POWERED_SPAWNER_ITEM,
            EIOBlocks.SOUL_ENGINE_ITEM);
        
        // Solar Panels (soul bindable)
        for (var solarPanelItem : EIOBlocks.SOLAR_PANEL_ITEMS.values()) {
            event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM,
                SoulCapabilityProviders.COMPONENT_SOUL_BINDABLE_PROVIDER,
                solarPanelItem);
        }

        // Register read-only soul bindable items
        // Soul vial uses a read-only ISoulBindable because the soul vial is a storage which can be used for binding, but is not directly bound to.
        event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM,
            SoulCapabilityProviders.READ_ONLY_COMPONENT_SOUL_BINDABLE_PROVIDER,
            EIOItems.SOUL_VIAL);

        // Register single soul handler items
        event.registerItem(EnderIOCapabilities.SOUL_HANDLER_ITEM,
            SoulCapabilityProviders.SINGLE_COMPONENT_SOUL_HANDLER_PROVIDER,
            EIOItems.SOUL_VIAL);

        // Conduit facades
        event.registerItem(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER, ComponentBackedConduitFacadeProvider.PROVIDER,
            EIOItems.CONDUIT_FACADE, EIOItems.TRANSPARENT_CONDUIT_FACADE, EIOItems.HARDENED_CONDUIT_FACADE, EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE);
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
