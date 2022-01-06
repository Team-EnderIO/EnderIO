package com.enderio.machines.client;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.model.IOOverlayModelLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MachinesClient {
    @SubscribeEvent
    public static void customModelLoaders(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(EIOMachines.loc("io_overlay"), new IOOverlayModelLoader());
    }

    @SubscribeEvent
    public static void textureStitch(TextureStitchEvent.Pre event) {
        // TODO: Stitch machine overlays
        event.addSprite(EIOMachines.loc("block/overlay/disabled"));
        event.addSprite(EIOMachines.loc("block/overlay/pull"));
        event.addSprite(EIOMachines.loc("block/overlay/push"));
        event.addSprite(EIOMachines.loc("block/overlay/push_pull"));
    }
}
