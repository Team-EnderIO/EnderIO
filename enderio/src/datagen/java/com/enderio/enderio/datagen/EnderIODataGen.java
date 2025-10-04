package com.enderio.enderio.datagen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.data.EIODataProvider;
import com.enderio.enderio.datagen.common.advancement.EIOAdvancementGenerator;
import com.enderio.enderio.datagen.common.advancement.MachinesAdvancementGenerator;
import com.enderio.enderio.datagen.common.data_maps.RangeExtenderDataMapProvider;
import com.enderio.enderio.datagen.common.datapack_registries.ConduitsBootstrap;
import com.enderio.enderio.datagen.common.data_maps.ReagentDataMapProvider;
import com.enderio.enderio.datagen.common.recipes.AlloyRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.BlockRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.ConduitRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.EnchanterRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.FermentingRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.FilterRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.FireCraftingRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.GlassRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.ItemRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.MachineRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.MaterialRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.PaintingRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.SagMillRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.SlicingRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.SoulBindingRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.TankRecipeProvider;
import com.enderio.enderio.datagen.common.recipes.WeatherChangeRecipeProvider;
import com.enderio.enderio.datagen.common.souldata.SoulDataProvider;
import com.enderio.enderio.datagen.common.tags.EIOBlockTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOEntityTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOFluidTagsProvider;
import com.enderio.enderio.datagen.common.tags.EIOItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
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

        provider.addSubProvider(event.includeServer(), new AdvancementProvider(packOutput, lookupProvider,
            existingFileHelper, List.of(new EIOAdvancementGenerator(), new MachinesAdvancementGenerator())));

        provider.addSubProvider(event.includeServer(), new MaterialRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new BlockRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new ItemRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new GlassRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new FireCraftingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new FilterRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new ConduitRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new MachineRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new AlloyRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new EnchanterRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new FermentingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new SagMillRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new SlicingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new SoulBindingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new TankRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new PaintingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new WeatherChangeRecipeProvider(packOutput, lookupProvider));

        provider.addSubProvider(event.includeServer(), new ReagentDataMapProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new RangeExtenderDataMapProvider(packOutput, lookupProvider));

        provider.addSubProvider(event.includeServer(), new SoulDataProvider(packOutput));

        generator.addProvider(true, provider);
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIORegistries.Keys.CONDUIT, ConduitsBootstrap::bootstrap);
    }
}
