package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.attachment.CoordinateSelection;
import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.capacitor.CapacitorData;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.common.filter.soul.EnderSoulFilter;
import com.enderio.base.common.filter.fluid.EnderFluidFilter;
import com.enderio.base.common.filter.item.general.EnderItemFilter;
import com.enderio.base.common.paint.BlockPaintData;
import com.enderio.core.common.util.NamedFluidContents;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIODataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister
            .createDataComponents(EnderIO.NAMESPACE);

    public static final Supplier<DataComponentType<SimpleFluidContent>> ITEM_FLUID_CONTENT = DATA_COMPONENT_TYPES
            .registerComponentType("item_fluid_content", builder -> builder.persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static final Supplier<DataComponentType<NamedFluidContents>> NAMED_FLUID_CONTENTS = DATA_COMPONENT_TYPES
            .registerComponentType("named_fluid_contents", builder -> builder.persistent(NamedFluidContents.CODEC)
                    .networkSynchronized(NamedFluidContents.STREAM_CODEC));

    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT_TYPES.registerComponentType("energy",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<BlockPaintData>> BLOCK_PAINT = DATA_COMPONENT_TYPES.registerComponentType(
            "block_paint",
            builder -> builder.persistent(BlockPaintData.CODEC).networkSynchronized(BlockPaintData.STREAM_CODEC));

    public static final Supplier<DataComponentType<Soul>> SOUL = DATA_COMPONENT_TYPES
            .registerComponentType("soul", builder -> builder.persistent(Soul.CODEC)
                    .networkSynchronized(Soul.STREAM_CODEC));

    public static final Supplier<DataComponentType<Float>> ENTITY_MAX_HEALTH = DATA_COMPONENT_TYPES
            .registerComponentType("entity_max_health", builder -> builder.persistent(Codec.FLOAT)
                    .networkSynchronized(ByteBufCodecs.FLOAT));

    public static final Supplier<DataComponentType<CapacitorData>> CAPACITOR_DATA = DATA_COMPONENT_TYPES
            .registerComponentType("capacitor_data",
                    builder -> builder.persistent(CapacitorData.CODEC).networkSynchronized(CapacitorData.STREAM_CODEC));

    public static final Supplier<DataComponentType<Boolean>> TOGGLED = DATA_COMPONENT_TYPES.registerComponentType("toggled",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<CoordinateSelection>> COORDINATE_SELECTION = DATA_COMPONENT_TYPES
            .registerComponentType("coordinate_selection", builder -> builder.persistent(CoordinateSelection.CODEC)
                    .networkSynchronized(CoordinateSelection.STREAM_CODEC));

    public static final Supplier<DataComponentType<GrindingBallData>> GRINDING_BALL = DATA_COMPONENT_TYPES
            .registerComponentType("grinding_ball", builder -> builder.persistent(GrindingBallData.CODEC)
                    .networkSynchronized(GrindingBallData.STREAM_CODEC));

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

    public static final Supplier<DataComponentType<Boolean>> TRAVEL_ITEM = DATA_COMPONENT_TYPES.registerComponentType(
            "travel_item", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static void register(IEventBus bus) {
        // Remap entity to soul
        DATA_COMPONENT_TYPES.addAlias(EnderIO.loc("stored_entity"), EnderIO.loc("soul"));
        DATA_COMPONENT_TYPES.addAlias(EnderIO.loc("entity_filter"), EnderIO.loc("soul_filter"));

        DATA_COMPONENT_TYPES.register(bus);
    }
}
