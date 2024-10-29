package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundCDataSlotUpdate(BlockPos pos, byte[] slotData) implements CustomPacketPayload {

    public static final Type<ServerboundCDataSlotUpdate> TYPE = new Type<>(EnderCore.loc("s2c_data_slot_update"));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCDataSlotUpdate> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ServerboundCDataSlotUpdate::pos,
        ByteBufCodecs.BYTE_ARRAY,
        ServerboundCDataSlotUpdate::slotData,
        ServerboundCDataSlotUpdate::new);
    // @formatter:on

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
