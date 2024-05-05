package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record S2CDataSlotUpdate(BlockPos pos, byte[] slotData) implements CustomPacketPayload {

    public static final Type<S2CDataSlotUpdate> TYPE =  new Type<>(EnderCore.loc("s2c_data_slot_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CDataSlotUpdate> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        S2CDataSlotUpdate::pos,
        ByteBufCodecs.BYTE_ARRAY,
        S2CDataSlotUpdate::slotData,
        S2CDataSlotUpdate::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
