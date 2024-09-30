package com.enderio.conduits.common.init;

import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.block.ConduitBundleBlockEntity;
import com.enderio.regilite.blockentities.DeferredBlockEntityType;
import com.enderio.regilite.blockentities.RegiliteBlockEntityTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ConduitBlockEntities {
    private static final RegiliteBlockEntityTypes BLOCK_ENTITY_TYPES = EnderIOConduits.REGILITE.blockEntityTypes();

    public static final DeferredBlockEntityType<ConduitBundleBlockEntity> CONDUIT = BLOCK_ENTITY_TYPES
        .create("conduit", ConduitBundleBlockEntity::new, ConduitBlocks.CONDUIT).finish();

    @SubscribeEvent
    public static void registerConduitCapabilities(RegisterCapabilitiesEvent event) {
        EnderIOConduitsRegistries.CONDUIT_TYPE.entrySet().stream()
            .flatMap(e -> e.getValue().exposedCapabilities().stream())
            .forEach(e -> registerConduitCapability(event, e));
    }

    private static <TCap, TContext> void registerConduitCapability(RegisterCapabilitiesEvent event, BlockCapability<TCap, TContext> capability) {
        event.registerBlockEntity(capability, CONDUIT.get(), ConduitBundleBlockEntity.createConduitCap(capability));
    }

    public static void register() {
    }
}
