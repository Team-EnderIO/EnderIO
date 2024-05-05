package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record C2SDataSlotChange(BlockPos pos, byte[] updateData) implements CustomPacketPayload {

    public static final Type<C2SDataSlotChange> TYPE = new Type<>(EnderCore.loc("c2s_data_slot_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SDataSlotChange> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        C2SDataSlotChange::pos,
        ByteBufCodecs.BYTE_ARRAY,
        C2SDataSlotChange::updateData,
        C2SDataSlotChange::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
