package com.enderio.conduits;

import com.enderio.EnderIO;
import com.enderio.base.data.EIODataProvider;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.init.ConduitTags;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.enderio.conduits.common.integrations.Integrations;
import com.enderio.conduits.data.ConduitTagProvider;
import com.enderio.conduits.data.recipe.ConduitRecipes;
import com.enderio.conduits.data.recipe.RedstoneFilterRecipes;
import com.enderio.conduits.data.tags.ConduitTagsProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = EnderIO.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EIOConduits {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        EnderIO.LOGGER.atDebug().log("================ Conduits construct ==================");
        IEventBus bus = EnderIO.modEventBus;

        EIOConduitTypes.register(bus);
        ConduitBlockEntities.register(bus);
        ConduitMenus.register(bus);
        ConduitBlocks.register(bus);
        ConduitItems.register(bus);
        ConduitComponents.register(bus);
        Integrations.register();
        ConduitLang.register();
        ConduitTags.register();
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        EIODataProvider provider = new EIODataProvider("conduits");

        provider.addSubProvider(event.includeServer(), new ConduitTagProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper()));
        provider.addSubProvider(event.includeServer(), new ConduitRecipes(packOutput, event.getLookupProvider()));
        provider.addSubProvider(event.includeServer(), new RedstoneFilterRecipes(packOutput, event.getLookupProvider()));
        provider.addSubProvider(event.includeServer(), new ConduitTagsProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper()));

        event.getGenerator().addProvider(true, provider);
    }
}
