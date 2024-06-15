package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConduitComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(EnderIO.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ExtractionSpeedUpgrade>> ITEM_SPEED_UPGRADE = DATA_COMPONENT_TYPES
        .registerComponentType("extraction_speed_upgrade", builder -> builder.persistent(ExtractionSpeedUpgrade.CODEC).networkSynchronized(ExtractionSpeedUpgrade.STREAM_CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
