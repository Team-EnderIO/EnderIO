package com.enderio.conduits;

import com.enderio.EnderIO;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.data.EIODataProvider;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.init.ConduitIngredientTypes;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.conduits.common.init.Conduits;
import com.enderio.conduits.common.integrations.Integrations;
import com.enderio.conduits.data.ConduitTagProvider;
import com.enderio.conduits.data.recipe.ConduitRecipes;
import com.enderio.conduits.data.recipe.RedstoneFilterRecipes;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber(modid = EnderIO.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EIOConduits {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        EnderIO.LOGGER.atDebug().log("================ Conduits construct ==================");
        IEventBus bus = EnderIO.modEventBus;

        Conduits.register(bus);
        ConduitTypes.register(bus);
        ConduitBlockEntities.register(bus);
        ConduitMenus.register(bus);
        ConduitBlocks.register(bus);
        ConduitItems.register(bus);
        ConduitComponents.register(bus);
        ConduitIngredientTypes.register(bus);
        Integrations.register();
        ConduitLang.register();
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent event) {
        var pack = event.getGenerator().getVanillaPack(true);
        var registries = event.getLookupProvider();

        var datapackEntriesProvider = pack.addProvider(output -> new DatapackBuiltinEntriesProvider(output, registries,
            createDatapackEntriesBuilder(), Set.of(EnderIO.MODID)));

        PackOutput packOutput = event.getGenerator().getPackOutput();

        EIODataProvider provider = new EIODataProvider("conduits");

        provider.addSubProvider(event.includeServer(), new ConduitTagProvider(packOutput, registries, event.getExistingFileHelper()));
        provider.addSubProvider(event.includeServer(), new ConduitRecipes(packOutput, datapackEntriesProvider.getRegistryProvider()));
        provider.addSubProvider(event.includeServer(), new RedstoneFilterRecipes(packOutput, registries));

        event.getGenerator().addProvider(true, provider);
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIORegistries.Keys.CONDUIT, Conduits::bootstrap);
    }
}
