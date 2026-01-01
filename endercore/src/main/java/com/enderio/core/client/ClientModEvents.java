package com.enderio.core.client;

import com.enderio.core.EnderCore;
import com.enderio.core.client.model.EitherModelLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = EnderCore.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterLoaders event) {
        event.register(EnderCore.id("modloaded"), new EitherModelLoader());
    }
}
