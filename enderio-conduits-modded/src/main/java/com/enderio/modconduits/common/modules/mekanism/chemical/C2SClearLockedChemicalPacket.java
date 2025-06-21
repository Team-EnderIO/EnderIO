package com.enderio.modconduits.common.modules.mekanism.chemical;

import com.enderio.base.api.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SClearLockedChemicalPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<C2SClearLockedChemicalPacket> TYPE = new Type<>(EnderIO.loc("clear_locked_chemical"));

    public static final StreamCodec<ByteBuf, C2SClearLockedChemicalPacket> STREAM_CODEC = BlockPos.STREAM_CODEC
            .map(C2SClearLockedChemicalPacket::new, C2SClearLockedChemicalPacket::pos);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
