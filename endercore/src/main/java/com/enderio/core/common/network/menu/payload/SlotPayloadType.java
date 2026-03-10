package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ByIdMap;

import java.util.function.Function;
import java.util.function.IntFunction;

public enum SlotPayloadType {
    // Basic data types
    NULL(NullSlotPayload::new), INT(IntSlotPayload::new),
    FLOAT(FloatSlotPayload::new), LONG(LongSlotPayload::new),
    STRING(StringSlotPayload::new), BOOL(BoolSlotPayload::new),

    // MC data types
    BLOCK_POS(BlockPosSlotPayload::new), ITEM_STACK(ItemStackSlotPayload::new),
    FLUID_STACK(FluidStackSlotPayload::new),
    RESOURCE_LOCATION(ResourceLocationSlotPayload::new),

    // Tools for combining payloads.
    LIST(ListSlotPayload::new), PAIR(PairSlotPayload::new),;

    public static final IntFunction<SlotPayloadType> BY_ID = ByIdMap.continuous(SlotPayloadType::ordinal, values(),
            ByIdMap.OutOfBoundsStrategy.WRAP);

    private final Function<FriendlyByteBuf, ? extends SlotPayload> bufferReaderSupplier;

    SlotPayloadType(Function<FriendlyByteBuf, ? extends SlotPayload> bufferReaderSupplier) {
        this.bufferReaderSupplier = bufferReaderSupplier;
    }

    public static SlotPayload read(FriendlyByteBuf buf) {
        int ordinal = buf.readInt();
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }

        return values()[ordinal].bufferReaderSupplier.apply(buf);
    }
}
