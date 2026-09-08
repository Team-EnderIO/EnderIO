package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record ClientBoundRemoveCapacitorBankPacket(UUID uuid) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<ClientBoundRemoveCapacitorBankPacket> TYPE = new CustomPacketPayload.Type<>(EnderIO.rl("remove_capacitor_bank"));

    public static final StreamCodec<ByteBuf, ClientBoundRemoveCapacitorBankPacket> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC,
        ClientBoundRemoveCapacitorBankPacket::uuid,
        ClientBoundRemoveCapacitorBankPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
