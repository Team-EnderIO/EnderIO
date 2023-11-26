package com.enderio.conduits;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.base.data.EIODataProvider;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.integrations.Integrations;
import com.enderio.conduits.common.items.ConduitBlockItem;
import com.enderio.conduits.common.network.ConduitNetwork;
import com.enderio.conduits.data.ConduitTagProvider;
import com.enderio.conduits.data.recipe.ConduitRecipes;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOConduits {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        System.out.println("================ Conduits construct ==================");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ConduitTypes.register(bus);
        EnderConduitTypes.register();
        ConduitBlockEntities.register();
        ConduitMenus.register();
        ConduitBlocks.register();
        ConduitItems.register();
        Integrations.register();
        ConduitNetwork.register();
        ConduitLang.register();
        ConduitItemFactory.setFactory((type, properties) -> new ConduitBlockItem(type, ConduitBlocks.CONDUIT.get(), properties));
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        EIODataProvider provider = new EIODataProvider("conduits");

        provider.addSubProvider(event.includeServer(), new ConduitTagProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper()));
        provider.addSubProvider(event.includeServer(), new ConduitRecipes(packOutput));

        event.getGenerator().addProvider(true, provider);
    }
}
