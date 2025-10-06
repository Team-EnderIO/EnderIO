package com.enderio.enderio.init;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class EIOCapabilityProviders {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void markProxyableCapabilities(RegisterCapabilitiesEvent event) {
        // TODO: Review these
        event.setProxyable(EnderIOCapabilities.SIDE_CONFIG);
        event.setProxyable(EnderIOCapabilities.SOUL_BINDABLE_BLOCK);
        event.setProxyable(EnderIOCapabilities.SOUL_HANDLER_BLOCK);
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        registerConduitCapabilities(event);
    }

    // region Conduits

    private static void registerConduitCapabilities(RegisterCapabilitiesEvent event) {
        EnderIORegistries.CONDUIT_TYPE.entrySet()
            .stream()
            .flatMap(e -> e.getValue().exposedCapabilities().stream())
            .forEach(e -> registerConduitCapability(event, e));
    }

    private static <TCap, TContext> void registerConduitCapability(RegisterCapabilitiesEvent event,
        BlockCapability<TCap, TContext> capability) {
        event.registerBlockEntity(capability, EIOBlockEntities.CONDUIT.get(),
            ConduitBundleBlockEntity.createCapabilityProvider(capability));
    }

    // endregion
}
