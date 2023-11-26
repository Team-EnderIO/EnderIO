package com.enderio.base.common.network;

import com.enderio.api.travel.ITravelTarget;
import com.enderio.base.common.handler.TravelHandler;
import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.Optional;

public class RequestTravelPacket implements Packet {
    private final BlockPos pos;

    public RequestTravelPacket(BlockPos pos) {
        this.pos = pos;
    }


    public RequestTravelPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerHandler.handle(context, pos);
    }

    public static class Handler extends PacketHandler<RequestTravelPacket> {

        @Override
        public RequestTravelPacket fromNetwork(FriendlyByteBuf buf) {
            return new RequestTravelPacket(buf);
        }

        @Override
        public void toNetwork(RequestTravelPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }

    public static class ServerHandler {
        static void handle(NetworkEvent.Context context, BlockPos pos) {
            var player = context.getSender();

            if (player == null) {
                return;
            }

            TravelSavedData travelData = TravelSavedData.getTravelData(player.level());
            Optional<ITravelTarget> target = travelData.getTravelTarget(pos);

            // These errors should only ever be triggered if there's some form of desync
            if (!TravelHandler.canBlockTeleport(player)) {
                player.displayClientMessage(Component.nullToEmpty("ERROR: Cannot teleport"), true);
                return;
            }
            if (target.isEmpty()) {
                player.displayClientMessage(Component.nullToEmpty("ERROR: Destination not a valid target"), true);
                return;
            }
            // Eventually change the packet structure to include what teleport method was used so this range can be selected correctly
            int range = Math.max(target.get().getBlock2BlockRange(), target.get().getItem2BlockRange());
            if (pos.distSqr(player.getOnPos()) > range * range) {
                player.displayClientMessage(Component.nullToEmpty("ERROR: Too far"), true);
                return;
            }

            TravelHandler.blockTeleportTo(player.level(), player, target.get(), false);
        }
    }
}
