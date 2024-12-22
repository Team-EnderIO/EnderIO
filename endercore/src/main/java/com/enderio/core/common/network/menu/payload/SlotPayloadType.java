package com.enderio.core.common.network.menu.payload;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum SlotPayloadType {
    // Basic data types
    NULL(() -> NullSlotPayload.STREAM_CODEC), INT(() -> IntSlotPayload.STREAM_CODEC),
    FLOAT(() -> FloatSlotPayload.STREAM_CODEC), LONG(() -> LongSlotPayload.STREAM_CODEC),
    STRING(() -> StringSlotPayload.STREAM_CODEC), BOOL(() -> BoolSlotPayload.STREAM_CODEC),

    // MC data types
    BLOCK_POS(() -> BlockPosSlotPayload.STREAM_CODEC), ITEM_STACK(() -> ItemStackSlotPayload.STREAM_CODEC),
    FLUID_STACK(() -> FluidStackSlotPayload.STREAM_CODEC),
    RESOURCE_LOCATION(() -> ResourceLocationSlotPayload.STREAM_CODEC),

    // Tools for combining payloads.
    LIST(() -> ListSlotPayload.STREAM_CODEC), PAIR(() -> PairSlotPayload.STREAM_CODEC),;

    public static final IntFunction<SlotPayloadType> BY_ID = ByIdMap.continuous(SlotPayloadType::ordinal, values(),
            ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, SlotPayloadType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
            SlotPayloadType::ordinal);

    private final Supplier<StreamCodec<RegistryFriendlyByteBuf, ? extends SlotPayload>> streamCodecSupplier;

    SlotPayloadType(Supplier<StreamCodec<RegistryFriendlyByteBuf, ? extends SlotPayload>> streamCodecSupplier) {
        this.streamCodecSupplier = streamCodecSupplier;
    }

    public StreamCodec<RegistryFriendlyByteBuf, ? extends SlotPayload> streamCodec() {
        return streamCodecSupplier.get();
    }
}
