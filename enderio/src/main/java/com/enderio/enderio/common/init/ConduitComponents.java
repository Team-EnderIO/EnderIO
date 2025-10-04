package com.enderio.enderio.common.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.api.conduits.facade.FacadeType;
import com.enderio.enderio.common.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.common.content.filters.redstone.DoubleRedstoneChannel;
import com.enderio.enderio.common.content.filters.redstone.RedstoneCountFilter;
import com.enderio.enderio.common.content.filters.redstone.RedstoneTLatchFilter;
import com.enderio.enderio.common.content.filters.redstone.RedstoneTimerFilter;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConduitComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister
            .createDataComponents(EnderIO.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FacadeType>> FACADE_TYPE = DATA_COMPONENT_TYPES
            .registerComponentType("facade_type",
                    builder -> builder.persistent(FacadeType.CODEC).networkSynchronized(FacadeType.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DoubleRedstoneChannel.Component>> REDSTONE_AND_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_and_filter",
                    builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC)
                            .networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneCountFilter.Component>> REDSTONE_COUNT_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_count_filter",
                    builder -> builder.persistent(RedstoneCountFilter.Component.CODEC)
                            .networkSynchronized(RedstoneCountFilter.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DoubleRedstoneChannel.Component>> REDSTONE_NAND_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_nand_filter",
                    builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC)
                            .networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DoubleRedstoneChannel.Component>> REDSTONE_NOR_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_nor_filter",
                    builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC)
                            .networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> REDSTONE_NOT_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_not_filter", builder -> builder.persistent(Codec.unit(Unit.INSTANCE))
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DoubleRedstoneChannel.Component>> REDSTONE_OR_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_or_filter",
                    builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC)
                            .networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> REDSTONE_SENSOR_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_sensor_filter", builder -> builder.persistent(Codec.unit(Unit.INSTANCE))
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneTimerFilter.Component>> REDSTONE_TIMER_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_timer_filter",
                    builder -> builder.persistent(RedstoneTimerFilter.Component.CODEC)
                            .networkSynchronized(RedstoneTimerFilter.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneTLatchFilter.Component>> REDSTONE_TLATCH_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_tlatch_filter",
                    builder -> builder.persistent(RedstoneTLatchFilter.Component.CODEC)
                            .networkSynchronized(RedstoneTLatchFilter.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DoubleRedstoneChannel.Component>> REDSTONE_XNOR_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_xnor_filter",
                    builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC)
                            .networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DoubleRedstoneChannel.Component>> REDSTONE_XOR_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("redstone_xor_filter",
                    builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC)
                            .networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ConduitProbeItem.ProbeConfigData>> PROBE_CONFIG = DATA_COMPONENT_TYPES
            .registerComponentType("probe_config",
                    builder -> builder.persistent(ConduitProbeItem.ProbeConfigData.CODEC)
                            .networkSynchronized(ConduitProbeItem.ProbeConfigData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ConduitProbeItem.State>> PROBE_STATE = DATA_COMPONENT_TYPES
            .registerComponentType("probe_state",
                    builder -> builder.persistent(ConduitProbeItem.State.CODEC)
                            .networkSynchronized(ConduitProbeItem.State.STREAM_CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register("conduit", () -> EnderIODataComponents.CONDUIT);
        DATA_COMPONENT_TYPES.register(bus);
    }
}
