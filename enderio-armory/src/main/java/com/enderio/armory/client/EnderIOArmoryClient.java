package com.enderio.armory.client;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.client.renderer.ElytraUpgradeRenderLayer;
import com.enderio.armory.client.renderer.MultiEnergyBarDecorator;
import com.enderio.armory.client.renderer.SolarUpgradeRenderLayer;
import com.enderio.armory.common.init.ArmoryItems;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID, value = Dist.CLIENT)
@Mod(value = EnderIOArmory.MODULE_MOD_ID, dist = Dist.CLIENT)
public class EnderIOArmoryClient {

    public EnderIOArmoryClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(TravelToggleHandler::checkShiftStatus);
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

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(ArmoryItems.DARK_STEEL_SWORD, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_AXE, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_PICKAXE, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_HELMET, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_CHESTPLATE, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_LEGGINGS, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_BOOTS, MultiEnergyBarDecorator.INSTANCE);
    }

}
