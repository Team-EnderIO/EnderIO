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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIODataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister
            .createDataComponents(EnderIO.MOD_ID);

    // region Generic

    public static final Supplier<DataComponentType<SimpleFluidContent>> ITEM_FLUID_CONTENT = DATA_COMPONENT_TYPES
            .registerComponentType("item_fluid_content", builder -> builder.persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static final Supplier<DataComponentType<NamedFluidContents>> NAMED_FLUID_CONTENTS = DATA_COMPONENT_TYPES
            .registerComponentType("named_fluid_contents", builder -> builder.persistent(NamedFluidContents.CODEC)
                    .networkSynchronized(NamedFluidContents.STREAM_CODEC));

    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT_TYPES.registerComponentType("energy",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Soul>> SOUL = DATA_COMPONENT_TYPES
        .registerComponentType("soul", builder -> builder.persistent(Soul.CODEC)
            .networkSynchronized(Soul.STREAM_CODEC));

    // endregion

    // region Capacitors

    public static final Supplier<DataComponentType<CapacitorData>> CAPACITOR_DATA = DATA_COMPONENT_TYPES
        .registerComponentType("capacitor_data",
            builder -> builder.persistent(CapacitorData.CODEC).networkSynchronized(CapacitorData.STREAM_CODEC));

    // endregion

    // region Conduits

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FacadeType>> FACADE_TYPE = DATA_COMPONENT_TYPES
        .registerComponentType("facade_type",
            builder -> builder.persistent(FacadeType.CODEC).networkSynchronized(FacadeType.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ConduitProbeItem.ProbeConfigData>> PROBE_CONFIG = DATA_COMPONENT_TYPES
        .registerComponentType("probe_config",
            builder -> builder.persistent(ConduitProbeItem.ProbeConfigData.CODEC)
                .networkSynchronized(ConduitProbeItem.ProbeConfigData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ConduitProbeItem.State>> PROBE_STATE = DATA_COMPONENT_TYPES
        .registerComponentType("probe_state",
            builder -> builder.persistent(ConduitProbeItem.State.CODEC)
                .networkSynchronized(ConduitProbeItem.State.STREAM_CODEC));

    // endregion

    // region Filters

    public static final Supplier<DataComponentType<EnderItemFilter>> ITEM_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("item_filter", builder -> builder.persistent(EnderItemFilter.CODEC)
            .networkSynchronized(EnderItemFilter.STREAM_CODEC));

//    public static final Supplier<DataComponentType<ExistingItemStackFilter>> EXISTING_ITEM_STACK_FILTER = DATA_COMPONENT_TYPES
//            .registerComponentType("existing_item_stack_filter",
//                    builder -> builder.persistent(ExistingItemStackFilter.CODEC)
//                            .networkSynchronized(ExistingItemStackFilter.STREAM_CODEC));
//
//    public static final Supplier<DataComponentType<ModIdItemStackFilter>> MOD_ID_ITEM_STACK_FILTER = DATA_COMPONENT_TYPES
//            .registerComponentType("mod_id_item_stack_filter", builder -> builder.persistent(ModIdItemStackFilter.CODEC)
//                    .networkSynchronized(ModIdItemStackFilter.STREAM_CODEC));

    public static final Supplier<DataComponentType<EnderFluidFilter>> FLUID_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("fluid_filter", builder -> builder.persistent(EnderFluidFilter.CODEC)
            .networkSynchronized(EnderFluidFilter.STREAM_CODEC));

    public static final Supplier<DataComponentType<EnderSoulFilter>> SOUL_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("soul_filter", builder -> builder.persistent(EnderSoulFilter.CODEC)
            .networkSynchronized(EnderSoulFilter.STREAM_CODEC));

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

    // endregion

    // region Machines

    public static final Supplier<DataComponentType<IOConfig>> IO_CONFIG = savedAndSynced("io_config", IOConfig.CODEC,
        IOConfig.STREAM_CODEC);

    public static final Supplier<DataComponentType<RedstoneControl>> REDSTONE_CONTROL = savedAndSynced("redstone_control",
        RedstoneControl.CODEC, RedstoneControl.STREAM_CODEC);

    public static final Supplier<DataComponentType<ActionRange>> ACTION_RANGE = savedAndSynced("action_range",
        ActionRange.CODEC, ActionRange.STREAM_CODEC);

    public static final Supplier<DataComponentType<Boolean>> IS_RANGE_VISIBLE = savedAndSynced("is_range_visible", Codec.BOOL,
        ByteBufCodecs.BOOL);

    public static final Supplier<DataComponentType<AlloySmelterMode>> ALLOY_SMELTER_MODE = saved("alloy_smelter_mode",
        AlloySmelterMode.CODEC);
    public static final Supplier<DataComponentType<Integer>> ALLOY_SMELTER_PROCESSED_INPUTS = saved(
        "alloy_smelter_processed_inputs", Codec.INT);

    // Could use the GRINDING_BALL component, but then you could use a sag mill as a
    // grinding ball :P
    public static final Supplier<DataComponentType<GrindingBallData>> SAG_MILL_GRINDING_BALL = saved("sag_mill_grinding_ball",
        GrindingBallData.CODEC);
    public static final Supplier<DataComponentType<Integer>> SAG_MILL_GRINDING_BALL_DAMAGE = saved(
        "sag_mill_grinding_ball_damage", Codec.INT);

    // endregion

    // region Paint

    public static final Supplier<DataComponentType<BlockPaintData>> BLOCK_PAINT = DATA_COMPONENT_TYPES.registerComponentType(
        "block_paint",
        builder -> builder.persistent(BlockPaintData.CODEC).networkSynchronized(BlockPaintData.STREAM_CODEC));

    // endregion

    // region Tools

    public static final Supplier<DataComponentType<Boolean>> TOGGLED = DATA_COMPONENT_TYPES.registerComponentType("toggled",
        builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<CoordinateSelection>> COORDINATE_SELECTION = DATA_COMPONENT_TYPES
        .registerComponentType("coordinate_selection", builder -> builder.persistent(CoordinateSelection.CODEC)
            .networkSynchronized(CoordinateSelection.STREAM_CODEC));

    // endregion

    // region Travel

    public static final Supplier<DataComponentType<Boolean>> TRAVEL_ITEM = DATA_COMPONENT_TYPES.registerComponentType(
        "travel_item", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    // endregion

    // region Vials

    public static final Supplier<DataComponentType<Float>> ENTITY_MAX_HEALTH = DATA_COMPONENT_TYPES
        .registerComponentType("entity_max_health", builder -> builder.persistent(Codec.FLOAT)
            .networkSynchronized(ByteBufCodecs.FLOAT));

    // endregion

    public static void register(IEventBus bus) {
        // Remap entity to soul
        DATA_COMPONENT_TYPES.addAlias(EnderIO.rl("stored_entity"), EnderIO.rl("soul"));
        DATA_COMPONENT_TYPES.addAlias(EnderIO.rl("entity_filter"), EnderIO.rl("soul_filter"));

        DATA_COMPONENT_TYPES.register("conduit", () -> EnderIODataComponents.CONDUIT);
        DATA_COMPONENT_TYPES.register("grinding_ball", () -> EnderIODataComponents.GRINDING_BALL);

        DATA_COMPONENT_TYPES.register(bus);
    }

    private static <T> Supplier<DataComponentType<T>> saved(String name, Codec<T> codec) {
        return DATA_COMPONENT_TYPES.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
    }

    private static <T> Supplier<DataComponentType<T>> savedAndSynced(String name, Codec<T> codec,
        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return DATA_COMPONENT_TYPES.register(name,
            () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
    }
}
