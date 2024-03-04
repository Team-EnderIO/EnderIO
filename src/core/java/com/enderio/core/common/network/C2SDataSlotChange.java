package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record C2SDataSlotChange(BlockPos pos, byte[] updateData)
    implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderCore.loc("c2s_data_slot_update");

    public C2SDataSlotChange(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readByteArray());
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
        writeInto.writeByteArray(updateData);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
