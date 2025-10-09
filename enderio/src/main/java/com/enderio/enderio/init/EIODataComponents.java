package com.enderio.enderio.init;

import com.enderio.core.common.util.NamedFluidContents;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.api.attachment.CoordinateSelection;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.api.conduits.facade.FacadeType;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilter;
import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import com.enderio.enderio.content.filters.redstone.DoubleRedstoneChannel;
import com.enderio.enderio.content.filters.redstone.RedstoneCountFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTLatchFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilter;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import com.enderio.enderio.content.paint.BlockPaintData;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.io.IOConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class EIODataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EnderIO.MOD_ID);

    // region Generic

    public static final DataComponentType<SimpleFluidContent> ITEM_FLUID_CONTENT = register("item_fluid_content",
        builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static final DataComponentType<NamedFluidContents> NAMED_FLUID_CONTENTS = register("named_fluid_contents",
        builder -> builder.persistent(NamedFluidContents.CODEC).networkSynchronized(NamedFluidContents.STREAM_CODEC));

    public static final DataComponentType<Integer> ENERGY = register("energy", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DataComponentType<Soul> SOUL = register("soul", builder -> builder.persistent(Soul.CODEC).networkSynchronized(Soul.STREAM_CODEC));

    // endregion

    // region Capacitors

    public static final DataComponentType<CapacitorData> CAPACITOR_DATA = register("capacitor_data",
        builder -> builder.persistent(CapacitorData.CODEC).networkSynchronized(CapacitorData.STREAM_CODEC));

    // endregion

    // region Conduits

    public static final DataComponentType<FacadeType> FACADE_TYPE = register("facade_type",
        builder -> builder.persistent(FacadeType.CODEC).networkSynchronized(FacadeType.STREAM_CODEC));

    public static final DataComponentType<ConduitProbeItem.ProbeConfigData> PROBE_CONFIG = register("probe_config",
        builder -> builder.persistent(ConduitProbeItem.ProbeConfigData.CODEC).networkSynchronized(ConduitProbeItem.ProbeConfigData.STREAM_CODEC));

    public static final DataComponentType<ConduitProbeItem.State> PROBE_STATE = register("probe_state",
        builder -> builder.persistent(ConduitProbeItem.State.CODEC).networkSynchronized(ConduitProbeItem.State.STREAM_CODEC));

    // endregion

    // region Filters

    public static final DataComponentType<EnderItemFilter> ITEM_FILTER = register("item_filter",
        builder -> builder.persistent(EnderItemFilter.CODEC).networkSynchronized(EnderItemFilter.STREAM_CODEC));

    public static final DataComponentType<EnderFluidFilter> FLUID_FILTER = register("fluid_filter",
        builder -> builder.persistent(EnderFluidFilter.CODEC).networkSynchronized(EnderFluidFilter.STREAM_CODEC));

    public static final DataComponentType<EnderSoulFilter> SOUL_FILTER = register("soul_filter",
        builder -> builder.persistent(EnderSoulFilter.CODEC).networkSynchronized(EnderSoulFilter.STREAM_CODEC));

    public static final DataComponentType<DoubleRedstoneChannel.Component> REDSTONE_AND_FILTER = register("redstone_and_filter",
        builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC).networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DataComponentType<RedstoneCountFilter.Component> REDSTONE_COUNT_FILTER = register("redstone_count_filter",
        builder -> builder.persistent(RedstoneCountFilter.Component.CODEC).networkSynchronized(RedstoneCountFilter.Component.STREAM_CODEC));

    public static final DataComponentType<DoubleRedstoneChannel.Component> REDSTONE_NAND_FILTER = register("redstone_nand_filter",
        builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC).networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DataComponentType<DoubleRedstoneChannel.Component> REDSTONE_NOR_FILTER = register("redstone_nor_filter",
        builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC).networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DataComponentType<Unit> REDSTONE_NOT_FILTER = register("redstone_not_filter",
        builder -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    public static final DataComponentType<DoubleRedstoneChannel.Component> REDSTONE_OR_FILTER = register("redstone_or_filter",
        builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC).networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DataComponentType<Unit> REDSTONE_SENSOR_FILTER = register("redstone_sensor_filter",
        builder -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    public static final DataComponentType<RedstoneTimerFilter.Component> REDSTONE_TIMER_FILTER = register("redstone_timer_filter",
        builder -> builder.persistent(RedstoneTimerFilter.Component.CODEC).networkSynchronized(RedstoneTimerFilter.Component.STREAM_CODEC));

    public static final DataComponentType<RedstoneTLatchFilter.Component> REDSTONE_TLATCH_FILTER = register("redstone_tlatch_filter",
        builder -> builder.persistent(RedstoneTLatchFilter.Component.CODEC).networkSynchronized(RedstoneTLatchFilter.Component.STREAM_CODEC));

    public static final DataComponentType<DoubleRedstoneChannel.Component> REDSTONE_XNOR_FILTER = register("redstone_xnor_filter",
        builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC).networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DataComponentType<DoubleRedstoneChannel.Component> REDSTONE_XOR_FILTER = register("redstone_xor_filter",
        builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC).networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    // endregion

    // region Machines

    public static final DataComponentType<IOConfig> IO_CONFIG = savedAndSynced("io_config", IOConfig.CODEC, IOConfig.STREAM_CODEC);

    public static final DataComponentType<RedstoneControl> REDSTONE_CONTROL = savedAndSynced("redstone_control", RedstoneControl.CODEC,
        RedstoneControl.STREAM_CODEC);

    public static final DataComponentType<ActionRange> ACTION_RANGE = savedAndSynced("action_range", ActionRange.CODEC, ActionRange.STREAM_CODEC);

    public static final DataComponentType<Boolean> IS_RANGE_VISIBLE = savedAndSynced("is_range_visible", Codec.BOOL, ByteBufCodecs.BOOL);

    public static final DataComponentType<AlloySmelterMode> ALLOY_SMELTER_MODE = saved("alloy_smelter_mode", AlloySmelterMode.CODEC);
    public static final DataComponentType<Integer> ALLOY_SMELTER_PROCESSED_INPUTS = saved("alloy_smelter_processed_inputs", Codec.INT);

    // Could use the GRINDING_BALL component, but then you could use a sag mill as a
    // grinding ball :P
    public static final DataComponentType<GrindingBallData> SAG_MILL_GRINDING_BALL = saved("sag_mill_grinding_ball", GrindingBallData.CODEC);
    public static final DataComponentType<Integer> SAG_MILL_GRINDING_BALL_DAMAGE = saved("sag_mill_grinding_ball_damage", Codec.INT);

    // endregion

    // region Paint

    public static final DataComponentType<BlockPaintData> BLOCK_PAINT = register("block_paint",
        builder -> builder.persistent(BlockPaintData.CODEC).networkSynchronized(BlockPaintData.STREAM_CODEC));

    // endregion

    // region Tools

    public static final DataComponentType<Boolean> TOGGLED = register("toggled",
        builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<CoordinateSelection> COORDINATE_SELECTION = register("coordinate_selection",
        builder -> builder.persistent(CoordinateSelection.CODEC).networkSynchronized(CoordinateSelection.STREAM_CODEC));

    // endregion

    // region Travel

    public static final DataComponentType<Boolean> TRAVEL_ITEM = register("travel_item",
        builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    // endregion

    // region Vials

    public static final DataComponentType<Float> ENTITY_MAX_HEALTH = register("entity_max_health",
        builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));

    // endregion

    public static void register(IEventBus bus) {
        // Remap entity to soul
        DATA_COMPONENT_TYPES.addAlias(EnderIO.rl("stored_entity"), EnderIO.rl("soul"));
        DATA_COMPONENT_TYPES.addAlias(EnderIO.rl("entity_filter"), EnderIO.rl("soul_filter"));

        DATA_COMPONENT_TYPES.register("conduit", () -> EnderIODataComponents.CONDUIT);
        DATA_COMPONENT_TYPES.register("grinding_ball", () -> EnderIODataComponents.GRINDING_BALL);

        DATA_COMPONENT_TYPES.register(bus);
    }

    // Creates the object immediately to ensure we can use it for item creation.
    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        var componentType = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENT_TYPES.register(name, () -> componentType);
        return componentType;
    }

    private static <T> DataComponentType<T> saved(String name, Codec<T> codec) {
        return register(name, builder -> builder.persistent(codec));
    }

    private static <T> DataComponentType<T> savedAndSynced(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return register(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec));
    }
}
