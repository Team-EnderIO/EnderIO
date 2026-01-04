package com.enderio.modded_conduits.init;

import com.enderio.enderio.init.EIOCreativeTabs;
import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = ModdedConduits.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModdedConduitsCreativeTabs {

    @SubscribeEvent
    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        // Add modded conduits items to EnderIO's main creative tab
        if (event.getTabKey() == EIOCreativeTabs.MAIN) {
            // Add Mekanism items if the mod is loaded
            if (ModList.get().isLoaded("mekanism")) {
                event.accept(MekanismModule.BASIC_CHEMICAL_FILTER.get());
            }

            // Future: Add other modded items here when they're implemented
            // if (ModList.get().isLoaded("appeng2")) { ... }
            // if (ModList.get().isLoaded("refinedstorage")) { ... }
        }
    }
}
