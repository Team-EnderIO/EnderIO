package com.enderio.enderio.init;

import com.enderio.core.common.backports.DataComponentType;
import com.enderio.core.common.util.NamedFluidContents;
import com.enderio.enderio.api.attachment.CoordinateSelection;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.api.conduits.facade.FacadeType;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilter;
import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import com.enderio.enderio.content.filters.item.limited.LimitedItemFilter;
import com.enderio.enderio.content.filters.redstone.DoubleRedstoneChannel;
import com.enderio.enderio.content.filters.redstone.RedstoneCountFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTLatchFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilter;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import com.enderio.enderio.content.paint.BlockPaintData;
import com.enderio.enderio.foundation.EIONBTKeys;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.io.IOConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;

public class EIODataComponents {
    // region Generic

    public static final DataComponentType<SimpleFluidContent> ITEM_FLUID_CONTENT = register("item_fluid_content",
        builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static final DataComponentType<NamedFluidContents> NAMED_FLUID_CONTENTS = register("named_fluid_contents",
        builder -> builder.persistent(NamedFluidContents.CODEC).networkSynchronized(NamedFluidContents.STREAM_CODEC));

    public static final DataComponentType<Integer> ENERGY = register("energy", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

//    public static final DataComponentType<Soul> SOUL = register("soul", builder -> builder.persistent(Soul.CODEC).networkSynchronized(Soul.STREAM_CODEC));
    public static final DataComponentType<Soul> SOUL = new DataComponentType<>(tag -> {
        // Supports legacy 1.20.1 format
        if (tag.contains(BlockItem.BLOCK_ENTITY_TAG)) {
            var entityTag = tag.getCompound(BlockItem.BLOCK_ENTITY_TAG);
            if (tag.contains(EIONBTKeys.ENTITY_STORAGE)) {
                return Soul.loadFromNbt(entityTag.getCompound(EIONBTKeys.ENTITY_STORAGE));
            }
        }

        return Soul.loadFromNbt(tag.getCompound(EIONBTKeys.ENTITY_STORAGE));
    }, (tag, value) -> {
        if (value != null) {
            tag.put(EIONBTKeys.ENTITY_STORAGE, value.writeToNbt(new CompoundTag()));
        } else {
            tag.remove(EIONBTKeys.ENTITY_STORAGE);
        }
    });

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

    public static final DataComponentType<LimitedItemFilter> LIMITED_ITEM_FILTER = register("limited_item_filter",
        builder -> builder.persistent(LimitedItemFilter.CODEC).networkSynchronized(LimitedItemFilter.STREAM_CODEC));

    public static final DataComponentType<EnderFluidFilter> FLUID_FILTER = register("fluid_filter",
        builder -> builder.persistent(EnderFluidFilter.CODEC).networkSynchronized(EnderFluidFilter.STREAM_CODEC));

    public static final DataComponentType<EnderSoulFilter> SOUL_FILTER = register("soul_filter",
        builder -> builder.persistent(EnderSoulFilter.CODEC).networkSynchronized(EnderSoulFilter.STREAM_CODEC));

    public static final DataComponentType<DoubleRedstoneChannel.Component> REDSTONE_FILTER_DOUBLE_CHANNEL = register("redstone_filter_double_channel",
        builder -> builder.persistent(DoubleRedstoneChannel.Component.CODEC).networkSynchronized(DoubleRedstoneChannel.Component.STREAM_CODEC));

    public static final DataComponentType<RedstoneCountFilter.Component> REDSTONE_COUNT_FILTER = register("redstone_count_filter",
        builder -> builder.persistent(RedstoneCountFilter.Component.CODEC).networkSynchronized(RedstoneCountFilter.Component.STREAM_CODEC));

    public static final DataComponentType<RedstoneTimerFilter.Component> REDSTONE_TIMER_FILTER = register("redstone_timer_filter",
        builder -> builder.persistent(RedstoneTimerFilter.Component.CODEC).networkSynchronized(RedstoneTimerFilter.Component.STREAM_CODEC));

    public static final DataComponentType<RedstoneTLatchFilter.Component> REDSTONE_TLATCH_FILTER = register("redstone_tlatch_filter",
        builder -> builder.persistent(RedstoneTLatchFilter.Component.CODEC).networkSynchronized(RedstoneTLatchFilter.Component.STREAM_CODEC));

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

    public static final DataComponentType<CoordinateSelection> COORDINATE_SELECTION = new DataComponentType<>(
        tag -> {
            if (tag.contains(EIONBTKeys.COORDINATE_SELECTION)) {
                CompoundTag selectionnbt = tag.getCompound(EIONBTKeys.COORDINATE_SELECTION);

                var dimensionId = new ResourceLocation(selectionnbt.getString(EIONBTKeys.LEVEL));

                CoordinateSelection selection = new CoordinateSelection(ResourceKey.create(Registries.DIMENSION, dimensionId),
                    NbtUtils.readBlockPos(selectionnbt.getCompound(EIONBTKeys.BLOCK_POS)));
                return selection;
            }

            return null;
        },
        (tag, value) -> {
            if (value != null) {
                CompoundTag selectionnbt = new CompoundTag();
                selectionnbt.putString(EIONBTKeys.LEVEL, value.level().location().toString());
                selectionnbt.put(EIONBTKeys.BLOCK_POS, NbtUtils.writeBlockPos(value.pos()));
                tag.put(EIONBTKeys.COORDINATE_SELECTION, selectionnbt);
            } else {
                tag.remove(EIONBTKeys.COORDINATE_SELECTION);
            }
        }
    );

    // endregion

    // region Travel

    public static final DataComponentType<Boolean> TRAVEL_ITEM = register("travel_item",
        builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    // endregion

    // region Vials

    public static final DataComponentType<Float> ENTITY_MAX_HEALTH = new DataComponentType<>(tag -> tag.getFloat(EIONBTKeys.ENTITY_MAX_HEALTH),
        (tag, value) -> tag.putFloat(EIONBTKeys.ENTITY_MAX_HEALTH, value));

    // endregion
}
