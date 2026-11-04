package com.enderio.enderio.compat.jei;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.compat.jei.category.FireCraftingRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@Mod(value = EnderIO.MOD_ID, dist = Dist.CLIENT, depends = "jei")
public class EnderIOJEIClient {

    public EnderIOJEIClient(IEventBus modBus) {
       modBus.addListener(this::registerPip);
    }

    public void registerPip(RegisterPictureInPictureRenderersEvent event) {
        event.register(FireCraftingRenderer.State.class, FireCraftingRenderer::new);
    }
}
