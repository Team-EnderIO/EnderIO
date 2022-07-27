package com.enderio.base.client;

import com.enderio.EnderIO;
import com.enderio.core.client.model.composite.CompositeGeometryLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void customModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        // TODO: 1.19: Remove me :)
        event.register("composite_model", new CompositeGeometryLoader());
    }
    
    @SubscribeEvent
    public static void additionalModels(ModelEvent.RegisterAdditional event) {
        event.register(EnderIO.loc("item/wood_gear_helper"));
        event.register(EnderIO.loc("item/stone_gear_helper"));
        event.register(EnderIO.loc("item/iron_gear_helper"));
        event.register(EnderIO.loc("item/energized_gear_helper"));
        event.register(EnderIO.loc("item/vibrant_gear_helper"));
        event.register(EnderIO.loc("item/dark_bimetal_gear_helper"));
    }
}
