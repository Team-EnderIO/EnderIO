package com.enderio.enderio.compat.jei.category;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

//TODO: MOVE to appropriate place later with modload check
@EventBusSubscriber(Dist.CLIENT)
public class RegisterRenderer {

    @SubscribeEvent
    public static void registerPip(RegisterPictureInPictureRenderersEvent event) {
        event.register(FireCraftingRenderer.State.class, FireCraftingRenderer::new);
    }

}

