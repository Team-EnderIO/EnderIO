package com.enderio.enderio.datagen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.datagen.client.EIOLanguageProvider;
import com.enderio.enderio.datagen.client.models.EIOBlockStateProvider;
import com.enderio.enderio.datagen.client.models.EIOItemModelProvider;
import com.enderio.enderio.datagen.common.advancement.EIOAdvancementGenerator;
import com.enderio.enderio.datagen.common.advancement.MachinesAdvancementGenerator;
import com.enderio.enderio.datagen.common.data_maps.GrindingBallDataMapProvider;
import com.enderio.enderio.datagen.common.data_maps.RangeExtenderDataMapProvider;
import com.enderio.enderio.datagen.common.data_maps.ReagentDataMapProvider;
import com.enderio.enderio.datagen.common.datapack_registries.ConduitsBootstrap;
import com.enderio.enderio.datagen.common.loot.ChestLootProvider;
import com.enderio.enderio.datagen.common.loot.EIOBlockLootProvider;
import com.enderio.enderio.datagen.common.loot.EIOLootModifiersProvider;
import com.enderio.enderio.datagen.common.recipes.EnderIORecipeProvider;
import com.enderio.enderio.datagen.common.souldata.SoulDataProvider;
import com.enderio.enderio.datagen.common.tags.EIOBlockTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOEntityTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOFluidTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(EnderIOAPI.MOD_ID)
public class EnderIODataGen {

    public EnderIODataGen(IEventBus eventBus) {
        // Temp: Runs after Regilite.
        eventBus.addListener(EventPriority.LOWEST, this::onGatherData);
    }

    public void onGatherData(GatherDataEvent event) {
        // Create datapack registry objects
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), Set.of(EnderIO.MOD_ID));

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        var b = new EIOBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), b);
        generator.addProvider(event.includeServer(),
            new EIOItemTagsProvider(packOutput, lookupProvider, b.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(),
            new EIOFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(),
            new EIOEntityTagsProvider(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(), new AdvancementProvider(packOutput, lookupProvider,
            existingFileHelper, List.of(new EIOAdvancementGenerator(), new MachinesAdvancementGenerator())));

        generator.addProvider(event.includeServer(), new EnderIORecipeProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(), new GrindingBallDataMapProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ReagentDataMapProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new RangeExtenderDataMapProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(), new SoulDataProvider(packOutput));

        generator.addProvider(event.includeServer(), new EIOLootModifiersProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(),
            new LootTableProvider(packOutput, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(EIOBlockLootProvider::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ChestLootProvider::new, LootContextParamSets.CHEST)
            ), lookupProvider));

        generator.addProvider(event.includeClient(), new EIOItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new EIOBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new EIOLanguageProvider(packOutput));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIORegistries.Keys.CONDUIT, ConduitsBootstrap::bootstrap);
    }
}
