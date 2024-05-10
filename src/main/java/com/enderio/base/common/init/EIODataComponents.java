package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.api.attachment.CoordinateSelection;
import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.capacitor.CapacitorData;
import com.enderio.api.grindingball.GrindingBallData;
import com.enderio.base.common.paint.BlockPaintData;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIODataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EnderIO.MODID);

    public static Supplier<DataComponentType<SimpleFluidContent>> ITEM_FLUID_CONTENT = DATA_COMPONENT_TYPES.register("item_fluid_content",
        () -> DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());

    public static Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT_TYPES.register("energy",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static Supplier<DataComponentType<BlockPaintData>> BLOCK_PAINT = DATA_COMPONENT_TYPES.register("block_paint",
        () -> DataComponentType.<BlockPaintData>builder().persistent(BlockPaintData.CODEC).networkSynchronized(BlockPaintData.STREAM_CODEC).build());

    public static Supplier<DataComponentType<StoredEntityData>> STORED_ENTITY = DATA_COMPONENT_TYPES.register("stored_entity",
        () -> DataComponentType.<StoredEntityData>builder().persistent(StoredEntityData.CODEC).networkSynchronized(StoredEntityData.STREAM_CODEC).build());

    public static Supplier<DataComponentType<CapacitorData>> CAPACITOR_DATA = DATA_COMPONENT_TYPES.register("capacitor_data",
        () -> DataComponentType.<CapacitorData>builder().persistent(CapacitorData.CODEC).networkSynchronized(CapacitorData.STREAM_CODEC).build());

    public static Supplier<DataComponentType<Boolean>> TOGGLED = DATA_COMPONENT_TYPES.register("toggled",
        () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static Supplier<DataComponentType<CoordinateSelection>> COORDINATE_SELECTION = DATA_COMPONENT_TYPES.register("coordinate_selection",
        () -> DataComponentType.<CoordinateSelection>builder().persistent(CoordinateSelection.CODEC).networkSynchronized(CoordinateSelection.STREAM_CODEC).build());

    public static Supplier<DataComponentType<GrindingBallData>> GRINDING_BALL = DATA_COMPONENT_TYPES.register("grinding_ball",
        () -> DataComponentType.<GrindingBallData>builder().persistent(GrindingBallData.CODEC).networkSynchronized(GrindingBallData.STREAM_CODEC).build());

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
