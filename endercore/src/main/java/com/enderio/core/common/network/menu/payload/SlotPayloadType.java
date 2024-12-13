package com.enderio.core.common.network.menu.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public enum SlotPayloadType {
    INT(0, () -> IntSlotPayload.STREAM_CODEC),
    FLOAT(1, () -> FloatSlotPayload.STREAM_CODEC),
    ;

    public static final IntFunction<SlotPayloadType> BY_ID = ByIdMap.continuous(SlotPayloadType::id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, SlotPayloadType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SlotPayloadType::id);

    private final int id;
    private final Supplier<StreamCodec<RegistryFriendlyByteBuf, ? extends SlotPayload>> streamCodecSupplier;

    SlotPayloadType(int id, Supplier<StreamCodec<RegistryFriendlyByteBuf, ? extends SlotPayload>> streamCodecSupplier) {
        this.id = id;
        this.streamCodecSupplier = streamCodecSupplier;
    }

    public int id() {
        return id;
    }

    public StreamCodec<RegistryFriendlyByteBuf, ? extends SlotPayload> streamCodec() {
        return streamCodecSupplier.get();
    }
}
