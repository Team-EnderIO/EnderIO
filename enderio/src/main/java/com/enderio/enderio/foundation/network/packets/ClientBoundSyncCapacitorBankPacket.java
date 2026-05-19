package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.capacitor_bank.rework.NewCapacitorBankBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;
import java.util.UUID;

public record ClientBoundSyncCapacitorBankPacket(UUID uuid, long storedEnergy, long capacity, long added, long send, List<BlockPos> nodes) implements CustomPacketPayload {
    public static final Type<ClientBoundSyncCapacitorBankPacket> TYPE = new Type<>(EnderIO.rl("sync_capacitor_bank"));

    public static final StreamCodec<ByteBuf, ClientBoundSyncCapacitorBankPacket> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC,
        ClientBoundSyncCapacitorBankPacket::uuid,
        ByteBufCodecs.VAR_LONG,
        ClientBoundSyncCapacitorBankPacket::storedEnergy,
        ByteBufCodecs.VAR_LONG,
        ClientBoundSyncCapacitorBankPacket::capacity,
        ByteBufCodecs.VAR_LONG,
        ClientBoundSyncCapacitorBankPacket::added,
        ByteBufCodecs.VAR_LONG,
        ClientBoundSyncCapacitorBankPacket::send,
        BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list(NewCapacitorBankBlockEntity.MAX_SIZE)),
        ClientBoundSyncCapacitorBankPacket::nodes,
        ClientBoundSyncCapacitorBankPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
