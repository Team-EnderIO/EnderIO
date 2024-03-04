package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record S2CDataSlotUpdate(BlockPos pos, byte[] slotData) implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderCore.loc("s2c_data_slot_update");

    public S2CDataSlotUpdate(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readByteArray());
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
        writeInto.writeByteArray(slotData);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
