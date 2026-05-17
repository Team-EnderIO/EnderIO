package com.enderio.modded_conduits.common;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.modded_conduits.common.modules.ConduitCommonModule;
import com.enderio.modded_conduits.common.modules.cc_tweaked.CCConduitCommonModule;
import com.enderio.modded_conduits.common.modules.refinedstorage.RefinedStorageCommonModule;
import com.enderio.modded_conduits.config.ModdedConduitsConfig;
import com.enderio.modded_conduits.data.ModConduitRecipeProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Map.entry;

@Mod(value = ModdedConduits.MOD_ID)
@EventBusSubscriber(modid = ModdedConduits.MOD_ID)
public class ModdedConduits {
    public static final String MOD_ID = EnderIO.MOD_ID + "_modded_conduits";

    // TODO: 1.22 - our own mod id.

    private static final Map<String, Supplier<ConduitCommonModule>> CONDUIT_MODULES = Map.ofEntries(
//            entry(ModuleModIds.APPLIED_ENERGISTICS, () -> AE2ConduitsModule.INSTANCE),
//            entry(ModuleModIds.MEKANISM, () -> MekanismModule.INSTANCE),
            entry(ModuleModIds.CC_TWEAKED, () -> CCConduitCommonModule.INSTANCE),
            entry(ModuleModIds.REFINED_STORAGE, () -> RefinedStorageCommonModule.INSTANCE));

    public static IEventBus modEventBus;

    public ModdedConduits(IEventBus modEventBus, ModContainer modContainer) {
        ModdedConduits.modEventBus = modEventBus;
        
        // Register config files
        modContainer.registerConfig(ModConfig.Type.COMMON, ModdedConduitsConfig.COMMON_SPEC, "enderio/modded-conduits-common.toml");
        
        executeOnLoadedModules(module -> module.initialize(modEventBus));
    }

    public static void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        executeOnLoadedModules(module -> module.buildConduitConditions(conditions));
    }

    public static void executeOnLoadedModules(Consumer<ConduitCommonModule> action) {
        for (var module : CONDUIT_MODULES.entrySet()) {
            if (ModList.get().isLoaded(module.getKey())) {
                action.accept(module.getValue().get());
            }
        }
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), ModdedConduits::buildConduitConditions, Set.of(EnderIO.MOD_ID));
        event.createProvider(ModConduitRecipeProvider.Runner::new);
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder().add(EnderIORegistries.Keys.CONDUIT,
            (context) -> ModdedConduits.executeOnLoadedModules(module -> module.bootstrapConduits(context)));
    }
}
