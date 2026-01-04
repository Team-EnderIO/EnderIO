package com.enderio.modded_conduits.init;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilterItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = ModdedConduits.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModdedConduitsCapabilityProviders {
    
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        // Only register Mekanism capabilities if Mekanism is loaded
        if (ModList.get().isLoaded("mekanism")) {
            registerMekanismCapabilities(event);
        }
    }
    
    private static void registerMekanismCapabilities(RegisterCapabilitiesEvent event) {
        // Chemical Filter capabilities
        event.registerItem(
            MekanismModule.Capabilities.CHEMICAL_FILTER,
            EnderChemicalFilterItem.CHEMICAL_FILTER_PROVIDER,
            MekanismModule.BASIC_CHEMICAL_FILTER.get()
        );
        
        event.registerItem(
            EnderIOCapabilities.FILTER_MENU_PROVIDER,
            AbstractFilterItem.FILTER_MENU_PROVIDER,
            MekanismModule.BASIC_CHEMICAL_FILTER.get()
        );
    }
}
