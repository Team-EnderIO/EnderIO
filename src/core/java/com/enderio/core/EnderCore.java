package com.enderio.core;

import com.enderio.core.common.integration.Integrations;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.enderio.core.EnderCore.MODID;

// Little helper for logging and resource locations.
// This is because core has no access to base.
@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class EnderCore {
    // Stored here just to make sure its the same.
    // This definition is used *everywhere* else.
    public static final String MODID = "enderio";

    public static final Logger LOGGER = LogManager.getLogger(MODID + ":core");

    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        Integrations.register();
    }

    public static ResourceLocation loc(String name) {
        return new ResourceLocation(MODID, name);
    }
}
