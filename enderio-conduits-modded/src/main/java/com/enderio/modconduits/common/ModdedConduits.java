package com.enderio.modconduits.common;

import static java.util.Map.entry;

import com.enderio.base.api.EnderIO;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.modconduits.common.modules.ConduitCommonModule;
import com.enderio.modconduits.common.modules.Integrations;
import com.enderio.modconduits.common.modules.appeng.AE2ConduitsModule;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.enderio.modconduits.common.modules.refinedstorage.RefinedStorageCommonModule;
import com.enderio.modconduits.data.ModConduitRecipeProvider;
import com.enderio.regilite.Regilite;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ModdedConduits.MODULE_MOD_ID)
@Mod(ModdedConduits.MODULE_MOD_ID)
public class ModdedConduits {
    public static final String MODULE_MOD_ID = "enderio_conduits_modded";

    public static final Regilite REGILITE = new Regilite(EnderIO.NAMESPACE);

    private static final Map<String, Supplier<ConduitCommonModule>> CONDUIT_MODULES = Map.ofEntries(
            entry(ModuleModIds.APPLIED_ENERGISTICS, () -> AE2ConduitsModule.INSTANCE),
            entry(ModuleModIds.MEKANISM, () -> MekanismModule.INSTANCE),
            entry(ModuleModIds.REFINED_STORAGE, () -> RefinedStorageCommonModule.INSTANCE));

    public static IEventBus modEventBus;

    public ModdedConduits(IEventBus modEventBus) {
        REGILITE.register(modEventBus);
        ModdedConduits.modEventBus = modEventBus;
        Integrations.register();

        executeOnLoadedModules(module -> module.initialize(modEventBus));
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent event) {
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), ModdedConduits::buildConduitConditions,
                Set.of(EnderIO.NAMESPACE));

        PackOutput packOutput = event.getGenerator().getPackOutput();
        var registryProvider = event.getLookupProvider();

        event.getGenerator()
                .addProvider(event.includeServer(), new ModConduitRecipeProvider(packOutput, registryProvider));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder().add(EnderIOConduitsRegistries.Keys.CONDUIT,
                (context) -> executeOnLoadedModules(module -> module.bootstrapConduits(context)));
    }

    private static void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        executeOnLoadedModules(module -> module.buildConduitConditions(conditions));
    }

    public static void executeOnLoadedModules(Consumer<ConduitCommonModule> action) {
        for (var module : CONDUIT_MODULES.entrySet()) {
            if (ModList.get().isLoaded(module.getKey())) {
                action.accept(module.getValue().get());
            }
        }
    }
}
