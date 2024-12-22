package com.enderio.machines.common.network;

import com.enderio.EnderIOBase;
import com.enderio.machines.common.blocks.powered_spawner.SpawnerMachineTask;
import com.enderio.machines.common.souldata.SpawnerSoul;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Clientbound
public record PoweredSpawnerSoulPacket(Map<ResourceLocation, SpawnerSoul.SoulData> map) implements CustomPacketPayload {

    public static final Type<PoweredSpawnerSoulPacket> TYPE = new Type<>(EnderIOBase.loc("powered_spawner_soul"));

    public static StreamCodec<ByteBuf, PoweredSpawnerSoulPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, SpawnerSoul.STREAM_CODEC),
            PoweredSpawnerSoulPacket::map, PoweredSpawnerSoulPacket::new);

    public PoweredSpawnerSoulPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readResourceLocation,
                buff -> new SpawnerSoul.SoulData(buff.readResourceLocation(), buff.readInt(),
                        buff.readEnum(SpawnerMachineTask.SpawnType.class))));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
