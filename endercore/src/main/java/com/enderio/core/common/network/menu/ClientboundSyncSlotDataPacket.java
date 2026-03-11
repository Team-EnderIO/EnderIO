package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record ClientboundSyncSlotDataPacket(int containerId, List<PayloadPair> payloads) {

    public ClientboundSyncSlotDataPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readCollection(ArrayList::new, PayloadPair::new));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(containerId);
        buf.writeCollection(payloads, (b, i) -> i.encode(b));
    }

    public record PayloadPair(short index, SlotPayload payload) {
        public PayloadPair(FriendlyByteBuf buf) {
            this(buf.readShort(), SlotPayloadType.read(buf));
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeShort(index);
            payload.write(buf);
        }
    }
}
