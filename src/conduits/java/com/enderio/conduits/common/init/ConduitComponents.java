package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.components.FluidSpeedUpgrade;
import com.enderio.conduits.common.components.ItemSpeedUpgrade;
import com.enderio.conduits.common.redstone.RedstoneANDFilter;
import com.enderio.conduits.common.redstone.RedstoneCountFilter;
import com.enderio.conduits.common.redstone.RedstoneNANDFilter;
import com.enderio.conduits.common.redstone.RedstoneNORFilter;
import com.enderio.conduits.common.redstone.RedstoneNOTFilter;
import com.enderio.conduits.common.redstone.RedstoneORFilter;
import com.enderio.conduits.common.redstone.RedstoneSensorFilter;
import com.enderio.conduits.common.redstone.RedstoneTLatchFilter;
import com.enderio.conduits.common.redstone.RedstoneTimerFilter;
import com.enderio.conduits.common.redstone.RedstoneXNORFilter;
import com.enderio.conduits.common.redstone.RedstoneXORFilter;
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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneANDFilter>> REDSTONE_AND_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_and_filter", builder -> builder.persistent(RedstoneANDFilter.CODEC).networkSynchronized(RedstoneANDFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneCountFilter>> REDSTONE_COUNT_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_count_filter", builder -> builder.persistent(RedstoneCountFilter.CODEC).networkSynchronized(RedstoneCountFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneNANDFilter>> REDSTONE_NAND_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_nand_filter", builder -> builder.persistent(RedstoneNANDFilter.CODEC).networkSynchronized(RedstoneNANDFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneNORFilter>> REDSTONE_NOR_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_nor_filter", builder -> builder.persistent(RedstoneNORFilter.CODEC).networkSynchronized(RedstoneNORFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneNOTFilter>> REDSTONE_NOT_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_not_filter", builder -> builder.persistent(RedstoneNOTFilter.CODEC).networkSynchronized(RedstoneNOTFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneORFilter>> REDSTONE_OR_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_or_filter", builder -> builder.persistent(RedstoneORFilter.CODEC).networkSynchronized(RedstoneORFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneSensorFilter>> REDSTONE_SENSOR_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_sensor_filter", builder -> builder.persistent(RedstoneSensorFilter.CODEC).networkSynchronized(RedstoneSensorFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneTimerFilter>> REDSTONE_TIMER_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_timer_filter", builder -> builder.persistent(RedstoneTimerFilter.CODEC).networkSynchronized(RedstoneTimerFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneTLatchFilter>> REDSTONE_TLATCH_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_tlatch_filter", builder -> builder.persistent(RedstoneTLatchFilter.CODEC).networkSynchronized(RedstoneTLatchFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneXNORFilter>> REDSTONE_XNOR_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_xnor_filter", builder -> builder.persistent(RedstoneXNORFilter.CODEC).networkSynchronized(RedstoneXNORFilter.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneXORFilter>> REDSTONE_XOR_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("redstone_xor_filter", builder -> builder.persistent(RedstoneXORFilter.CODEC).networkSynchronized(RedstoneXORFilter.STREAM_CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
