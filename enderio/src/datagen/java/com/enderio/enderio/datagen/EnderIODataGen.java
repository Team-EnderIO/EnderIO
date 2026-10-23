package com.enderio.enderio.datagen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.datagen.client.EIOLanguageProvider;
import com.enderio.enderio.datagen.client.models.EIOModelProvider;
import com.enderio.enderio.datagen.client.models.block.AthenaProvider;
import com.enderio.enderio.datagen.common.advancement.EIOAdvancementGenerator;
import com.enderio.enderio.datagen.common.advancement.MachinesAdvancementGenerator;
import com.enderio.enderio.datagen.common.data_maps.SoulDataMapProvider;
import com.enderio.enderio.datagen.common.data_maps.GrindingBallDataMapProvider;
import com.enderio.enderio.datagen.common.data_maps.RangeExtenderDataMapProvider;
import com.enderio.enderio.datagen.common.data_maps.ReagentDataMapProvider;
import com.enderio.enderio.datagen.common.datapack_registries.ConduitsBootstrap;
import com.enderio.enderio.datagen.common.datapack_registries.TrimMaterialsBootstrap;
import com.enderio.enderio.datagen.common.loot.ChestLootProvider;
import com.enderio.enderio.datagen.common.loot.EIOBlockLootProvider;
import com.enderio.enderio.datagen.common.loot.EIOLootModifiersProvider;
import com.enderio.enderio.datagen.common.recipes.EnderIORecipeProvider;
import com.enderio.enderio.datagen.client.sounds.EIOSoundDefinitionProvider;
import com.enderio.enderio.datagen.common.tags.EIOBlockTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOEntityTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOFluidTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOItemTagsProvider;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(EnderIOAPI.MOD_ID)
public class EnderIODataGen {

    public EnderIODataGen(IEventBus eventBus) {
        eventBus.addListener(this::gatherDatapacks);
        eventBus.addListener(this::onGatherData);
    }

    @SuppressWarnings("NullableProblems")
    public void gatherDatapacks(GatherDataRegistryEntriesEvent event) {
        // World
        event.add(EnderIORegistries.Keys.CONDUIT, ConduitsBootstrap::bootstrap);
        event.add(Registries.TRIM_MATERIAL, TrimMaterialsBootstrap::bootstrap);

        // Reloadable
        event.add(Registries.ADVANCEMENT, new AdvancementProvider(List.of(EIOAdvancementGenerator::new, MachinesAdvancementGenerator::new)));
        event.add(Registries.LOOT_TABLE, new LootTableProvider(Set.of(), List.of(
            new LootTableProvider.SubProviderEntry(EIOBlockLootProvider::new, LootContextParamSets.BLOCK),
            new LootTableProvider.SubProviderEntry(ChestLootProvider::new, LootContextParamSets.CHEST)
        )));
        event.add(EnderIORecipeProvider.create());
    }

    public void onGatherData(GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        event.addProvider(new EIOBlockTagsProvider(packOutput, lookupProvider));
        event.addProvider(new EIOItemTagsProvider(packOutput, lookupProvider));
        event.addProvider(new EIOFluidTagsProvider(packOutput, lookupProvider));
        event.addProvider(new EIOEntityTagsProvider(packOutput, lookupProvider));

        event.addProvider(new GrindingBallDataMapProvider(packOutput, lookupProvider));
        event.addProvider(new ReagentDataMapProvider(packOutput, lookupProvider));
        event.addProvider(new RangeExtenderDataMapProvider(packOutput, lookupProvider));
        event.addProvider(new SoulDataMapProvider(packOutput, lookupProvider));

        event.addProvider(new EIOLootModifiersProvider(packOutput, lookupProvider));

        event.addProvider(new EIOModelProvider(packOutput));
        event.addProvider(new EIOLanguageProvider(packOutput));
        event.addProvider(new EIOSoundDefinitionProvider(packOutput));

        var athenaPack = event.getGenerator().getPackGenerator(true, "enderio_athena", EnderIO.ATHENA_RESOURCE_PACK_PATH);
        athenaPack.addProvider(AthenaProvider::new);
        athenaPack.addProvider(output -> new PackMetadataGenerator(output).add(PackMetadataSection.CLIENT_TYPE,
            new PackMetadataSection(EIOCommonLang.RESOURCE_PACK_ATHENA,
                DetectedVersion.BUILT_IN.packVersion(PackType.CLIENT_RESOURCES).minorRange())));
    }
}
