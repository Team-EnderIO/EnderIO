package com.enderio.base.common.network;

import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.INetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.PlayNetworkDirection;

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
        return context.getDirection() == PlayNetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        TravelSavedData travelData = TravelSavedData.getTravelData(null);
        travelData.loadNBT(this.data);
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
        public Optional<INetworkDirection<?>> getDirection() {
            return Optional.of(PlayNetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
