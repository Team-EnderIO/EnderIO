package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ConduitCapabilities {
    public static final Capability<ConduitUpgrade> CONDUIT_UPGRADE = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ConduitUpgrade.class);
    }
}
