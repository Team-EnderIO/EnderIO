package com.enderio.base.common.network;

import com.enderio.EnderIO;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FilterUpdatePacket(boolean nbt, boolean inverted) implements CustomPacketPayload {

    public static ResourceLocation ID = EnderIO.loc("filter_update");

    public FilterUpdatePacket(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(nbt);
        pBuffer.writeBoolean(inverted);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
