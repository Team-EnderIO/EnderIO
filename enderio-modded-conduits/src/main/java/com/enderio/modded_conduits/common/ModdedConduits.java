package com.enderio.modded_conduits.common;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.modded_conduits.common.modules.ConduitCommonModule;
import com.enderio.modded_conduits.common.modules.appeng.AE2ConduitsModule;
import com.enderio.modded_conduits.config.ModdedConduitsConfig;
import com.enderio.modded_conduits.data.ModConduitRecipeProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Map.entry;

@Mod(value = ModdedConduits.MOD_ID)
@EventBusSubscriber(modid = ModdedConduits.MOD_ID)
public class ModdedConduits {
    public static final String MOD_ID = EnderIO.MOD_ID + "_modded_conduits";

    private static final Map<String, Supplier<ConduitCommonModule>> CONDUIT_MODULES = Map.ofEntries(
            entry(ModuleModIds.APPLIED_ENERGISTICS, () -> AE2ConduitsModule.INSTANCE)
//            entry(ModuleModIds.MEKANISM, () -> MekanismModule.INSTANCE),
//            entry(ModuleModIds.REFINED_STORAGE, () -> RefinedStorageCommonModule.INSTANCE)
    );

    public static IEventBus modEventBus;

    public ModdedConduits(IEventBus modEventBus, ModContainer modContainer) {
        ModdedConduits.modEventBus = modEventBus;
        
        // Register config files
        modContainer.registerConfig(ModConfig.Type.SYNCED, ModdedConduitsConfig.COMMON_SPEC, "enderio/modded-conduits-synced.toml");
        
        executeOnLoadedModules(module -> module.initialize(modEventBus));
    }

    public static void executeOnLoadedModules(Consumer<ConduitCommonModule> action) {
        for (var module : CONDUIT_MODULES.entrySet()) {
            if (ModList.get().isLoaded(module.getKey())) {
                action.accept(module.getValue().get());
            }
        }
    }

    @SubscribeEvent
    public static void onDatapackGather(GatherDataRegistryEntriesEvent event) {
        event.add(EnderIORegistries.Keys.CONDUIT, bootstrap -> {
            ModdedConduits.executeOnLoadedModules(module -> module.bootstrapConduits(bootstrap));
        });
        event.add(ModConduitRecipeProvider.create());
    }
}
