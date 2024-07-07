package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.block.ConduitBundleBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockEntityRegistry;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = EnderIO.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConduitBlockEntities {
    private static final BlockEntityRegistry BLOCK_ENTITY_REGISTRY = EnderIO.getRegilite().blockEntityRegistry();

    public static final RegiliteBlockEntity<ConduitBundleBlockEntity> CONDUIT = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("conduit", ConduitBundleBlockEntity::new, ConduitBlocks.CONDUIT);

    @SubscribeEvent
    public static void registerConduitCapabilities(RegisterCapabilitiesEvent event) {
        Set<BlockCapability<?, Direction>> capabilities = EnderIORegistries.CONDUIT_TYPE.entrySet().stream()
            .flatMap(e -> e.getValue().exposedCapabilities().stream())
            .collect(Collectors.toUnmodifiableSet());

        for (var capability : capabilities) {
            registerConduitCapability(event, capability);
        }
    }

    private static <T> void registerConduitCapability(RegisterCapabilitiesEvent event, BlockCapability<T, Direction> capability) {
        event.registerBlockEntity(capability, CONDUIT.get(), ConduitBundleBlockEntity.createConduitCap(capability));
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTRY.register(bus);
    }
}
