package com.enderio.armory.client;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.init.ArmoryItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(ArmoryItems.DARK_STEEL_SWORD, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_AXE, MultiEnergyBarDecorator.INSTANCE);
        event.register(ArmoryItems.DARK_STEEL_PICKAXE, MultiEnergyBarDecorator.INSTANCE);
    }

}
