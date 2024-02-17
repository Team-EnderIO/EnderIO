package com.enderio.machines.common.network;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.enderio.machines.common.souldata.SpawnerSoul;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

// Clientbound
public record PoweredSpawnerSoulPacket(Map<ResourceLocation, SpawnerSoul.SoulData> map)
    implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderIO.loc("powered_spawner_soul");

    public PoweredSpawnerSoulPacket(FriendlyByteBuf buf) {
        this(
            buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
                new SpawnerSoul.SoulData(buff.readResourceLocation(), buff.readInt(), buff.readEnum(SpawnerMachineTask.SpawnType.class))
            )
        );
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeMap(map, FriendlyByteBuf::writeResourceLocation, (buf, soulData) -> {
            buf.writeResourceLocation(soulData.entitytype());
            buf.writeInt(soulData.power());
            buf.writeEnum(soulData.spawnType());
        });
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> SpawnerSoul.SPAWNER.map = this.map);
        context.setPacketHandled(true);
    }
}
