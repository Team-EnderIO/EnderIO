package com.enderio.base.common.network;

import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public class RemoveTravelTargetPacket implements Packet {
    private final BlockPos pos;

    public RemoveTravelTargetPacket(BlockPos pos) {
        this.pos = pos;
    }


    public RemoveTravelTargetPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ClientHandler.handle(pos);
    }

    public static class Handler extends Packet.PacketHandler<RemoveTravelTargetPacket> {

        @Override
        public RemoveTravelTargetPacket fromNetwork(FriendlyByteBuf buf) {
            return new RemoveTravelTargetPacket(buf);
        }

        @Override
        public void toNetwork(RemoveTravelTargetPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static class ClientHandler {
        static void handle(BlockPos pos) {
            TravelSavedData travelData = TravelSavedData.getTravelData(Minecraft.getInstance().level);
            travelData.removeTravelTargetAt(Minecraft.getInstance().level, pos);
        }
    }
}
