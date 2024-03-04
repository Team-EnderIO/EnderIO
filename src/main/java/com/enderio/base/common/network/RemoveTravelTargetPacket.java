package com.enderio.base.common.network;

import com.enderio.EnderIO;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemoveTravelTargetPacket(BlockPos pos) implements CustomPacketPayload {

    public static ResourceLocation ID = EnderIO.loc("remove_travel_target");

    public RemoveTravelTargetPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
