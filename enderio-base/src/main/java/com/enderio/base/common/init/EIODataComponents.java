package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.attachment.CoordinateSelection;
import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.api.capacitor.CapacitorData;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.common.capability.EntityFilterCapability;
import com.enderio.base.common.capability.FluidFilterCapability;
import com.enderio.base.common.filter.ExistingItemStackFilter;
import com.enderio.base.common.filter.EnderItemStackFilter;
import com.enderio.base.common.filter.ModIdItemStackFilter;
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

    public static Supplier<DataComponentType<SimpleFluidContent>> ITEM_FLUID_CONTENT = DATA_COMPONENT_TYPES
            .registerComponentType("item_fluid_content", builder -> builder.persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static Supplier<DataComponentType<NamedFluidContents>> NAMED_FLUID_CONTENTS = DATA_COMPONENT_TYPES
            .registerComponentType("named_fluid_contents", builder -> builder.persistent(NamedFluidContents.CODEC)
                    .networkSynchronized(NamedFluidContents.STREAM_CODEC));

    public static Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT_TYPES.registerComponentType("energy",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static Supplier<DataComponentType<BlockPaintData>> BLOCK_PAINT = DATA_COMPONENT_TYPES.registerComponentType(
            "block_paint",
            builder -> builder.persistent(BlockPaintData.CODEC).networkSynchronized(BlockPaintData.STREAM_CODEC));

    public static Supplier<DataComponentType<StoredEntityData>> STORED_ENTITY = DATA_COMPONENT_TYPES
            .registerComponentType("stored_entity", builder -> builder.persistent(StoredEntityData.CODEC)
                    .networkSynchronized(StoredEntityData.STREAM_CODEC));

    public static Supplier<DataComponentType<CapacitorData>> CAPACITOR_DATA = DATA_COMPONENT_TYPES
            .registerComponentType("capacitor_data",
                    builder -> builder.persistent(CapacitorData.CODEC).networkSynchronized(CapacitorData.STREAM_CODEC));

    public static Supplier<DataComponentType<Boolean>> TOGGLED = DATA_COMPONENT_TYPES.registerComponentType("toggled",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static Supplier<DataComponentType<CoordinateSelection>> COORDINATE_SELECTION = DATA_COMPONENT_TYPES
            .registerComponentType("coordinate_selection", builder -> builder.persistent(CoordinateSelection.CODEC)
                    .networkSynchronized(CoordinateSelection.STREAM_CODEC));

    public static Supplier<DataComponentType<GrindingBallData>> GRINDING_BALL = DATA_COMPONENT_TYPES
            .registerComponentType("grinding_ball", builder -> builder.persistent(GrindingBallData.CODEC)
                    .networkSynchronized(GrindingBallData.STREAM_CODEC));

    public static final Supplier<DataComponentType<EnderItemStackFilter>> ITEM_STACK_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("item_filter", builder -> builder.persistent(EnderItemStackFilter.CODEC)
                    .networkSynchronized(EnderItemStackFilter.STREAM_CODEC));

    public static final Supplier<DataComponentType<ExistingItemStackFilter>> EXISTING_ITEM_STACK_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("existing_item_stack_filter", builder -> builder.persistent(ExistingItemStackFilter.CODEC)
            .networkSynchronized(ExistingItemStackFilter.STREAM_CODEC));

    public static final Supplier<DataComponentType<ModIdItemStackFilter>> MOD_ID_ITEM_STACK_FILTER = DATA_COMPONENT_TYPES
        .registerComponentType("mod_id_item_stack_filter", builder -> builder.persistent(ModIdItemStackFilter.CODEC)
            .networkSynchronized(ModIdItemStackFilter.STREAM_CODEC));

    public static final Supplier<DataComponentType<FluidFilterCapability.Component>> FLUID_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("fluid_filter", builder -> builder.persistent(FluidFilterCapability.Component.CODEC)
                    .networkSynchronized(FluidFilterCapability.Component.STREAM_CODEC));

    public static final Supplier<DataComponentType<EntityFilterCapability.Component>> ENTITY_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("entity_filter",
                    builder -> builder.persistent(EntityFilterCapability.Component.CODEC)
                            .networkSynchronized(EntityFilterCapability.Component.STREAM_CODEC));

    public static Supplier<DataComponentType<Boolean>> TRAVEL_ITEM = DATA_COMPONENT_TYPES.registerComponentType(
            "travel_item", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
