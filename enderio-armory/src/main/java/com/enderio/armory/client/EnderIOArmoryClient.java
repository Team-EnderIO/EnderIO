package com.enderio.armory.client;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.client.renderer.ElytraUpgradeRenderLayer;
import com.enderio.armory.client.renderer.SolarUpgradeRenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
@Mod(value = EnderIOArmory.MODULE_MOD_ID, dist = Dist.CLIENT)
public class EnderIOArmoryClient {

    public EnderIOArmoryClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer
                        .addLayer(new ElytraUpgradeRenderLayer<>(playerRenderer, event.getContext().getModelSet()));
                playerRenderer.addLayer(new SolarUpgradeRenderLayer(playerRenderer));
            }
        }
    }

}
