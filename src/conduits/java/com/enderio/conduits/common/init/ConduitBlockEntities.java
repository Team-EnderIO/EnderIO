package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockEntityRegistry;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = EnderIO.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConduitBlockEntities {
    private static final BlockEntityRegistry BLOCK_ENTITY_REGISTRY = EnderIO.getRegilite().blockEntityRegistry();

    public static final RegiliteBlockEntity<ConduitBlockEntity> CONDUIT = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("conduit", ConduitBlockEntity::new, ConduitBlocks.CONDUIT);

    @SubscribeEvent
    public static void registerConduitCapabilities(RegisterCapabilitiesEvent event) {
        // TODO: Come back and resolve this with a custom RegisterConduitCapabilitiesEvent?
//        Set<BlockCapability<?, Direction>> capabilities = EnderIORegistries.CONDUIT_NETWORK_TYPES.entrySet().stream()
//            .flatMap(e -> e.getValue().getExposedCapabilities().stream())
//            .collect(Collectors.toUnmodifiableSet());
//
//        for (var capability : capabilities) {
//            registerConduitCapability(event, capability);
//        }
    }

    private static <T> void registerConduitCapability(RegisterCapabilitiesEvent event, BlockCapability<T, Direction> capability) {
        event.registerBlockEntity(capability, CONDUIT.get(), ConduitBlockEntity.createConduitCap(capability));
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTRY.register(bus);
    }
}
