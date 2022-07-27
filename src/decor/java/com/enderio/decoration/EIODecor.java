package com.enderio.decoration;

import com.enderio.EnderIO;
import com.enderio.decoration.common.config.DecorConfig;
import com.enderio.decoration.common.init.DecorBlockEntities;
import com.enderio.decoration.common.init.DecorBlocks;
import com.enderio.decoration.common.init.DecorEntities;
import com.enderio.decoration.common.network.EnderDecorNetwork;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIODecor {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        // Register config
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, DecorConfig.COMMON_SPEC, "enderio/decor-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, DecorConfig.CLIENT_SPEC, "enderio/decor-client.toml");

        // Perform classloads for everything so things are registered.
        DecorBlocks.register();
        DecorBlockEntities.register();
        DecorEntities.register();
        EnderDecorNetwork.register();
    }

    // GatherDataEvent too!
}
