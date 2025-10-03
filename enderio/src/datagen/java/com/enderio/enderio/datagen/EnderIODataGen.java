package com.enderio.enderio.datagen;

import com.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.conduits.common.init.Conduits;
import com.enderio.enderio.data.EIODataProvider;
import com.enderio.enderio.datagen.datapack_registries.ConduitsBootstrap;
import com.enderio.enderio.datagen.recipes.ConduitRecipes;
import com.enderio.enderio.datagen.tags.EIOBlockTagsProvider;
import com.enderio.enderio.datagen.tags.EIOEntityTagsProvider;
import com.enderio.enderio.datagen.tags.EIOFluidTagsProvider;
import com.enderio.enderio.datagen.tags.EIOItemTagsProvider;
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

@Mod(EnderIOAPI.MOD_ID)
public class EnderIODataGen {

    public EnderIODataGen(IEventBus eventBus) {
        eventBus.addListener(EventPriority.LOWEST, this::onGatherData);
    }

    public void onGatherData(GatherDataEvent event) {
        // Create datapack registry objects
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), Set.of(EnderIO.MOD_ID));

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // TODO: Remove this wrapper...
        EIODataProvider provider = new EIODataProvider("new");

        var b = new EIOBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        provider.addSubProvider(event.includeServer(), b);
        provider.addSubProvider(event.includeServer(),
            new EIOItemTagsProvider(packOutput, lookupProvider, b.contentsGetter(), existingFileHelper));
        provider.addSubProvider(event.includeServer(),
            new EIOFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        provider.addSubProvider(event.includeServer(),
            new EIOEntityTagsProvider(packOutput, lookupProvider, existingFileHelper));

        provider.addSubProvider(event.includeServer(), new ConduitRecipes(packOutput, lookupProvider));

        generator.addProvider(true, provider);
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIORegistries.Keys.CONDUIT, ConduitsBootstrap::bootstrap);
    }
}
