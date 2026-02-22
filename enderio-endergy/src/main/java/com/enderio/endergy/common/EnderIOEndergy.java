package com.enderio.endergy.common;

import com.enderio.endergy.common.init.EndergyBlocks;
import com.enderio.endergy.common.init.EndergyCreativeTabs;
import com.enderio.endergy.common.init.EndergyItems;
import com.enderio.endergy.common.item.TotemicCapacitorItem;
import com.enderio.enderio.api.EnderIOCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(EnderIOEndergy.MOD_ID)
public class EnderIOEndergy {
    public static final String MOD_ID = "enderio_endergy";

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public EnderIOEndergy(IEventBus eventBus) {
        EndergyItems.register(eventBus);
        EndergyBlocks.register(eventBus);
        EndergyCreativeTabs.register(eventBus);

        eventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(EnderIOCapabilities.CAPACITOR_EXTENSION, (stack, v) -> (TotemicCapacitorItem)stack.getItem(), EndergyItems.TOTEMIC_CAPACITOR);
    }
}
