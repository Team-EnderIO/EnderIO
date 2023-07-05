package com.enderio.machines.common.network;

import com.enderio.core.common.network.Packet;
import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.enderio.machines.common.souldata.SpawnerSoul;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PoweredSpawnerSoulPacket implements Packet {
    public static Map<ResourceLocation, SpawnerSoul.SoulData> SYNCED_DATA = new HashMap<>();

    private final Map<ResourceLocation, SpawnerSoul.SoulData> map;

    public PoweredSpawnerSoulPacket(Map<ResourceLocation, SpawnerSoul.SoulData> map) {
        this.map = map;
    }

    public PoweredSpawnerSoulPacket(FriendlyByteBuf buf) {
        Map<ResourceLocation, SpawnerSoul.SoulData> newMap = new HashMap<>();
        buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
            new SpawnerSoul.SoulData(buff.readResourceLocation(), buff.readInt(), buff.readEnum(SpawnerMachineTask.SpawnType.class))
        );
        this.map = newMap;
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeMap(map, FriendlyByteBuf::writeResourceLocation, (buf, soulData) -> {
            buf.writeResourceLocation(soulData.entitytype());
            buf.writeInt(soulData.power());
            buf.writeEnum(soulData.spawnType());
        });
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> SYNCED_DATA = this.map);
        context.setPacketHandled(true);
    }

    public static class Handler extends PacketHandler<PoweredSpawnerSoulPacket> {

        @Override
        public PoweredSpawnerSoulPacket fromNetwork(FriendlyByteBuf buf) {
            return new PoweredSpawnerSoulPacket(buf);
        }

        @Override
        public void toNetwork(PoweredSpawnerSoulPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
