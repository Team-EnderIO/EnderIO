package com.enderio.base.common.network;

import com.enderio.EnderIO;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncTravelDataPacket(CompoundTag data) implements CustomPacketPayload {
    public static ResourceLocation ID = EnderIO.loc("sync_travel_data");

    public SyncTravelDataPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeNbt(this.data);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
