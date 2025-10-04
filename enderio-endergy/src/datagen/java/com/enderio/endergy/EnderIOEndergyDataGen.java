package com.enderio.endergy;

import com.enderio.endergy.common.EnderIOEndergy;
import com.enderio.endergy.datapack_registries.ConduitsBootstrap;
import com.enderio.enderio.api.EnderIORegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(EnderIOEndergy.MOD_ID)
public class EnderIOEndergyDataGen {
    public EnderIOEndergyDataGen(IEventBus eventBus) {
        eventBus.addListener(EventPriority.LOWEST, this::onGatherData);
    }

    public void onGatherData(GatherDataEvent event) {
        // Create datapack registry objects
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), Set.of(EnderIOEndergy.MOD_ID));

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // TODO: Remove this wrapper...
//        provider.addSubProvider(event.includeServer(), new ConduitRecipes(packOutput, lookupProvider));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIORegistries.Keys.CONDUIT, ConduitsBootstrap::bootstrap);
    }
}
