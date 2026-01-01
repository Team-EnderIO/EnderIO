package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.powered_spawner.MobSpawnMode;
import com.enderio.enderio.foundation.souldata.SpawnerSoul;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record ClientboundPoweredSpawnerSoulPacket(Map<Identifier, SpawnerSoul.SoulData> map) implements CustomPacketPayload {

    public static final Type<ClientboundPoweredSpawnerSoulPacket> TYPE = new Type<>(EnderIO.id("powered_spawner_soul"));

    public static final StreamCodec<ByteBuf, ClientboundPoweredSpawnerSoulPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, SpawnerSoul.STREAM_CODEC),
            ClientboundPoweredSpawnerSoulPacket::map, ClientboundPoweredSpawnerSoulPacket::new);

    public ClientboundPoweredSpawnerSoulPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readIdentifier,
                buff -> new SpawnerSoul.SoulData(buff.readIdentifier(), buff.readInt(),
                        buff.readEnum(MobSpawnMode.class))));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
