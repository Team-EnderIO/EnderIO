package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.components.FluidSpeedUpgrade;
import com.enderio.conduits.common.components.ItemSpeedUpgrade;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConduitComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(EnderIO.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemSpeedUpgrade>> ITEM_SPEED_UPGRADE = DATA_COMPONENT_TYPES
        .registerComponentType("item_speed_upgrade", builder -> builder.persistent(ItemSpeedUpgrade.CODEC).networkSynchronized(ItemSpeedUpgrade.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidSpeedUpgrade>> FLUID_SPEED_UPGRADE = DATA_COMPONENT_TYPES
        .registerComponentType("fluid_speed_upgrade", builder -> builder.persistent(FluidSpeedUpgrade.CODEC).networkSynchronized(FluidSpeedUpgrade.STREAM_CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
