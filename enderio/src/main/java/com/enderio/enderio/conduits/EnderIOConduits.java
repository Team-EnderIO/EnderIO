package com.enderio.enderio.conduits;

import com.enderio.EnderIOBase;
import com.enderio.enderio.api.EnderIO;
import com.enderio.enderio.conduits.modded.common.ModdedConduits;
import com.enderio.enderio.conduits.modded.data.ModConduitRecipeProvider;
import com.enderio.enderio.data.EIODataProvider;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.enderio.conduits.common.init.ConduitComponents;
import com.enderio.enderio.conduits.common.init.ConduitIngredientTypes;
import com.enderio.enderio.conduits.common.init.ConduitItems;
import com.enderio.enderio.conduits.common.init.ConduitLang;
import com.enderio.enderio.conduits.common.init.ConduitMenus;
import com.enderio.enderio.conduits.common.init.ConduitTypes;
import com.enderio.enderio.conduits.common.init.Conduits;
import com.enderio.enderio.conduits.common.integrations.Integrations;
import com.enderio.enderio.conduits.data.ConduitTagProvider;
import com.enderio.enderio.conduits.data.recipe.ConduitRecipes;
import com.enderio.enderio.conduits.integration.ftb_ultimine.FTBUltimineCompat;
import com.enderio.regilite.Regilite;
import java.util.Set;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
@Mod(EnderIO.MOD_ID)
public class EnderIOConduits {
    public static final Regilite REGILITE = EnderIOBase.REGILITE;

    public EnderIOConduits(IEventBus modEventBus, ModContainer modContainer) {
        Conduits.register();
        ConduitTypes.register(modEventBus);
        ConduitBlockEntities.register(modEventBus);
        ConduitMenus.register(modEventBus);
        ConduitBlocks.register(modEventBus);
        ConduitItems.register(modEventBus);
        ConduitComponents.register(modEventBus);
        ConduitIngredientTypes.register(modEventBus);
        Integrations.register();
        ConduitLang.register();

        if (ModList.get().isLoaded("ftbultimine")) {
            FTBUltimineCompat.init();
        }
    }

    @SubscribeEvent
    public static void onNewRegistries(NewRegistryEvent event) {
        event.register(EnderIORegistries.CONDUIT_TYPE);
        event.register(EnderIORegistries.CONDUIT_DATA_TYPE);
        event.register(EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE);
        event.register(EnderIORegistries.CONDUIT_NODE_DATA_TYPE);
        event.register(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_TYPE);
    }

    @SubscribeEvent
    private static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(EnderIORegistries.Keys.CONDUIT, Conduit.DIRECT_CODEC, Conduit.DIRECT_CODEC);
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent event) {
        // Includes ModdedConduits datagen
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder(), ModdedConduits::buildConduitConditions, Set.of(EnderIO.MOD_ID));

        PackOutput packOutput = event.getGenerator().getPackOutput();
        var registries = event.getLookupProvider();

        EIODataProvider provider = new EIODataProvider("conduits");

        provider.addSubProvider(event.includeServer(),
                new ConduitTagProvider(packOutput, registries, event.getExistingFileHelper()));

        provider.addSubProvider(event.includeServer(), new ConduitRecipes(packOutput, registries));

        event.getGenerator().addProvider(true, provider);

        event.getGenerator()
            .addProvider(event.includeServer(), new ModConduitRecipeProvider(packOutput, registries));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
            .add(EnderIORegistries.Keys.CONDUIT, (context) -> {
                Conduits.bootstrap(context);
                ModdedConduits.executeOnLoadedModules(module -> module.bootstrapConduits(context));
            });
    }
}
