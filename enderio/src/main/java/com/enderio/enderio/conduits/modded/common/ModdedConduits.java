package com.enderio.enderio.conduits.modded.common;

import static java.util.Map.entry;

import com.enderio.EnderIOBase;
import com.enderio.enderio.api.EnderIO;
import com.enderio.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.enderio.conduits.modded.common.modules.ConduitCommonModule;
import com.enderio.enderio.conduits.modded.common.modules.Integrations;
import com.enderio.enderio.conduits.modded.common.modules.appeng.AE2ConduitsModule;
import com.enderio.enderio.conduits.modded.common.modules.mekanism.MekanismModule;
import com.enderio.enderio.conduits.modded.common.modules.refinedstorage.RefinedStorageCommonModule;
import com.enderio.enderio.conduits.modded.data.ModConduitRecipeProvider;
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

@Mod(ModdedConduits.MODULE_MOD_ID)
public class ModdedConduits {
    public static final String MODULE_MOD_ID = EnderIO.NAMESPACE;

    public static final Regilite REGILITE = EnderIOBase.REGILITE;

    private static final Map<String, Supplier<ConduitCommonModule>> CONDUIT_MODULES = Map.ofEntries(
            entry(ModuleModIds.APPLIED_ENERGISTICS, () -> AE2ConduitsModule.INSTANCE),
            entry(ModuleModIds.MEKANISM, () -> MekanismModule.INSTANCE),
            entry(ModuleModIds.REFINED_STORAGE, () -> RefinedStorageCommonModule.INSTANCE));

    public static IEventBus modEventBus;

    public ModdedConduits(IEventBus modEventBus) {
        ModdedConduits.modEventBus = modEventBus;
        Integrations.register();

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
}
