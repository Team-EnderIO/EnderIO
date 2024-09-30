package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundDataSlotChange(BlockPos pos, byte[] updateData) implements CustomPacketPayload {

    public static final Type<ClientboundDataSlotChange> TYPE = new Type<>(EnderCore.loc("c2s_data_slot_update"));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDataSlotChange> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ClientboundDataSlotChange::pos,
        ByteBufCodecs.BYTE_ARRAY,
        ClientboundDataSlotChange::updateData,
        ClientboundDataSlotChange::new);
    // @formatter:on

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
