package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public interface SlotPayload {

    SlotPayloadType type();

    void write(FriendlyByteBuf buf);
}
