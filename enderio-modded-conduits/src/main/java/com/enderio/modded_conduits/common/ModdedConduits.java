package com.enderio.modded_conduits.common;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.modded_conduits.common.modules.ConduitCommonModule;
import com.enderio.modded_conduits.common.modules.appeng.AE2ConduitsModule;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import com.enderio.modded_conduits.common.modules.refinedstorage.RefinedStorageCommonModule;
import com.enderio.modded_conduits.config.ModdedConduitsConfig;
import com.enderio.modded_conduits.data.ModConduitRecipeProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Map.entry;

@Mod(value = ModdedConduits.MOD_ID)
@Mod.EventBusSubscriber(modid = ModdedConduits.MOD_ID)
public class ModdedConduits {
    public static final String MOD_ID = EnderIO.MOD_ID + "_modded_conduits";

    // TODO: 1.22 - our own mod id.

    private static final Map<String, Supplier<ConduitCommonModule>> CONDUIT_MODULES = Map.ofEntries(
            entry(ModuleModIds.APPLIED_ENERGISTICS, () -> AE2ConduitsModule.INSTANCE),
            entry(ModuleModIds.MEKANISM, () -> MekanismModule.INSTANCE),
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
    public static void onData(GatherDataEvent event) {
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), ModdedConduits::buildConduitConditions, Set.of(EnderIO.MOD_ID));

        PackOutput packOutput = event.getGenerator().getPackOutput();
        var registries = event.getLookupProvider();

        event.getGenerator()
            .addProvider(event.includeServer(), new ModConduitRecipeProvider(packOutput, registries));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder().add(EnderIORegistries.Keys.CONDUIT,
            (context) -> ModdedConduits.executeOnLoadedModules(module -> module.bootstrapConduits(context)));
    }
}
