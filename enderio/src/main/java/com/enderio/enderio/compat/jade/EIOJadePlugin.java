package com.enderio.enderio.compat.jade;

// 26.2-port: third-party mod interaction commented out — Jade integration deferred
// import snownee.jade.api.BlockAccessor;
// import snownee.jade.api.IWailaClientRegistration;
// import snownee.jade.api.IWailaCommonRegistration;
// import snownee.jade.api.IWailaPlugin;
// import snownee.jade.api.WailaPlugin;

import com.enderio.enderio.EnderIO;
import net.minecraft.resources.Identifier;

// 26.2-port: @WailaPlugin annotation removed — Jade plugin is disabled
// @WailaPlugin
public class EIOJadePlugin /* 26.2-port: implements IWailaPlugin */ {

    public static final Identifier SOUL_BOUND_COMPONENT = EnderIO.id("soul_bound");

    // 26.2-port: Jade integration disabled — methods are no-ops
    // @Override
    // public void registerClient(IWailaClientRegistration registration) {
    //     registration.addRayTraceCallback((_, accessor, _) -> { ... });
    //     registration.registerBlockComponent(SoulBoundComponentProvider.INSTANCE, Block.class);
    // }

    // @Override
    // public void register(IWailaCommonRegistration registration) {
    //     registration.registerBlockDataProvider(SoulBoundServerDataProvider.INSTANCE, Block.class);
    // }
}
