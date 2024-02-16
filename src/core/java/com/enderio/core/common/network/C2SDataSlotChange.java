package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record C2SDataSlotChange(BlockPos pos, @Nullable FriendlyByteBuf updateData)
    implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderCore.loc("s2c_data_slot_update");

    public C2SDataSlotChange(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), new FriendlyByteBuf(buf.copy()));
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
        writeInto.writeBytes(updateData);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
