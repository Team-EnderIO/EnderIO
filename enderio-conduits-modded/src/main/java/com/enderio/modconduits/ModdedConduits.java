package com.enderio.modconduits;

import com.enderio.EnderIOBase;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.modconduits.mods.appeng.AE2ConduitsModule;
import com.enderio.modconduits.data.ModConduitRecipeProvider;
import com.enderio.modconduits.mods.mekanism.MekanismModule;
import com.enderio.regilite.Regilite;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Map.entry;

@EventBusSubscriber(modid = ModdedConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@Mod(ModdedConduits.MODULE_MOD_ID)
public class ModdedConduits {
    public static final String MODULE_MOD_ID = "enderio_conduits_modded";
    public static final String REGISTRY_NAMESPACE = EnderIOBase.REGISTRY_NAMESPACE;

    public static final Regilite REGILITE = new Regilite(REGISTRY_NAMESPACE);

    private static final Map<String, Supplier<ConduitModule>> CONDUIT_MODULES = Map.ofEntries(
        entry("ae2", () -> AE2ConduitsModule.INSTANCE),
        entry("mekanism", () -> MekanismModule.INSTANCE)
    );

    public ModdedConduits(IEventBus modEventBus) {
        REGILITE.register(modEventBus);

        executeOnLoadedModules(module -> module.register(modEventBus));
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent event) {
        var pack = event.getGenerator().getVanillaPack(true);

        var datapackEntriesProvider = pack.addProvider(output -> new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(),
            createDatapackEntriesBuilder(), ModdedConduits::buildConduitConditions, Set.of(REGISTRY_NAMESPACE)));

        PackOutput packOutput = event.getGenerator().getPackOutput();
        var registryProvider = datapackEntriesProvider.getRegistryProvider();

        event.getGenerator().addProvider(event.includeServer(), new ModConduitRecipeProvider(packOutput, registryProvider));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIOConduitsRegistries.Keys.CONDUIT, (context) -> executeOnLoadedModules(module -> module.bootstrapConduits(context)));
    }

    private static void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        executeOnLoadedModules(module -> module.buildConduitConditions(conditions));
    }

    public static void executeOnLoadedModules(Consumer<ConduitModule> action) {
        for (var module : CONDUIT_MODULES.entrySet()) {
            if (ModList.get().isLoaded(module.getKey())) {
                action.accept(module.getValue().get());
            }
        }
    }
}
