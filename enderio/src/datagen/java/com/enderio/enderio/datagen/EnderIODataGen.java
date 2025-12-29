package com.enderio.enderio.datagen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.datagen.client.EIOLanguageProvider;
import com.enderio.enderio.datagen.client.models.EIOModelProvider;
import com.enderio.enderio.datagen.common.advancement.EIOAdvancementGenerator;
import com.enderio.enderio.datagen.common.advancement.MachinesAdvancementGenerator;
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
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(EnderIOAPI.MOD_ID)
public class EnderIODataGen {

    public EnderIODataGen(IEventBus eventBus) {
        eventBus.addListener(this::onGatherData);
    }

    // TODO: 1.21.8: investigate split between Client and Server properly https://docs.neoforged.net/docs/1.21.8/resources/#data-generation
    public void onGatherData(GatherDataEvent.Client event) {
        // Create datapack registry objects
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), Set.of(EnderIO.MOD_ID));

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        var b = new EIOBlockTagsProvider(packOutput, lookupProvider);
        generator.addProvider(true, b);
        generator.addProvider(true,
            new EIOItemTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true,
            new EIOFluidTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true,
            new EIOEntityTagsProvider(packOutput, lookupProvider));

        generator.addProvider(true, new AdvancementProvider(packOutput, lookupProvider,
            List.of(new EIOAdvancementGenerator(), new MachinesAdvancementGenerator())));

        //generator.addProvider(true, new EnderIORecipeProvider(lookupProvider.get(), packOutput));
        event.createProvider(EnderIORecipeProvider.Runner::new);

        generator.addProvider(true, new ReagentDataMapProvider(packOutput, lookupProvider));
        generator.addProvider(true, new RangeExtenderDataMapProvider(packOutput, lookupProvider));

        generator.addProvider(true, new SoulDataProvider(packOutput));

        generator.addProvider(true, new EIOLootModifiersProvider(packOutput, lookupProvider));

        generator.addProvider(true,
            new LootTableProvider(packOutput, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(EIOBlockLootProvider::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ChestLootProvider::new, LootContextParamSets.CHEST)
            ), lookupProvider));

        generator.addProvider(true, new EIOModelProvider(packOutput));
        //generator.addProvider(true, new EIOItemModelProvider(packOutput));
        //generator.addProvider(true, new EIOBlockStateProvider(packOutput));
        generator.addProvider(true, new EIOLanguageProvider(packOutput));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIORegistries.Keys.CONDUIT, ConduitsBootstrap::bootstrap);
    }
}
