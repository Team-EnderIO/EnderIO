package com.enderio.base.common.network;

import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public class SyncTravelDataPacket implements Packet {

    private final CompoundTag data;
    public SyncTravelDataPacket(CompoundTag tag) {
        this.data = tag;
    }
    public SyncTravelDataPacket(FriendlyByteBuf buf) {
        this.data = buf.readNbt();
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeNbt(this.data);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> TravelSavedData.getTravelData(Minecraft.getInstance().level).loadNBT(this.data.copy()));
        context.setPacketHandled(true);
    }

    public static class Handler extends PacketHandler<SyncTravelDataPacket>{

        @Override
        public SyncTravelDataPacket fromNetwork(FriendlyByteBuf buf) {
            return new SyncTravelDataPacket(buf);
        }

        @Override
        public void toNetwork(SyncTravelDataPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
