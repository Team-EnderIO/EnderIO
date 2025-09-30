package com.enderio.enderio.conduits.common.init;

import com.enderio.enderio.conduits.EnderIOConduits;
import com.enderio.enderio.api.conduits.EnderIOConduitsRegistries;
import com.enderio.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockEntityRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class ConduitBlockEntities {
    private static final BlockEntityRegistry BLOCK_ENTITY_REGISTRY = EnderIOConduits.REGILITE.blockEntityRegistry();

    public static final RegiliteBlockEntity<ConduitBundleBlockEntity> CONDUIT = BLOCK_ENTITY_REGISTRY
            .registerBlockEntity("conduit", ConduitBundleBlockEntity::new, ConduitBlocks.CONDUIT);

    @SubscribeEvent
    public static void registerConduitCapabilities(RegisterCapabilitiesEvent event) {
        EnderIOConduitsRegistries.CONDUIT_TYPE.entrySet()
                .stream()
                .flatMap(e -> e.getValue().exposedCapabilities().stream())
                .forEach(e -> registerConduitCapability(event, e));
    }

    private static <TCap, TContext> void registerConduitCapability(RegisterCapabilitiesEvent event,
            BlockCapability<TCap, TContext> capability) {
        event.registerBlockEntity(capability, CONDUIT.get(),
                ConduitBundleBlockEntity.createCapabilityProvider(capability));
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTRY.register(bus);
    }
}
