package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import net.minecraft.network.FriendlyByteBuf;

public record ServerboundSetSyncSlotDataPacket(int containerId, short index, SlotPayload payload) {
    public ServerboundSetSyncSlotDataPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readShort(), SlotPayloadType.read(buf));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(containerId);
        buf.writeShort(index);
        payload.write(buf);
    }
}
