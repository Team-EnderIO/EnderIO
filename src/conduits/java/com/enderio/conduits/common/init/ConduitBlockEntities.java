package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockEntityRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ConduitBlockEntities {
    private static final BlockEntityRegistry BLOCK_ENTITY_REGISTRY = BlockEntityRegistry.create(EnderIO.MODID);

    public static final RegiliteBlockEntity<ConduitBlockEntity> CONDUIT = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("conduit", ConduitBlockEntity::new, ConduitBlocks.CONDUIT)
        //TODO: NEO-PORT:
        .addCapability(Capabilities.EnergyStorage.BLOCK, ConduitBlockEntity.createConduitCap(Capabilities.EnergyStorage.BLOCK))
        .addCapability(Capabilities.FluidHandler.BLOCK, ConduitBlockEntity.createConduitCap(Capabilities.FluidHandler.BLOCK))
        .addCapability(Capabilities.ItemHandler.BLOCK, ConduitBlockEntity.createConduitCap(Capabilities.ItemHandler.BLOCK));

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTRY.register(bus);
    }
}
