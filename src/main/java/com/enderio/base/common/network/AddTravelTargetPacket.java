package com.enderio.base.common.network;

import com.enderio.api.travel.ITravelTarget;
import com.enderio.api.travel.TravelRegistry;
import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.INetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AddTravelTargetPacket implements Packet {

    @Nullable
    private final ITravelTarget target;

    public AddTravelTargetPacket(ITravelTarget target) {
        this.target = target;
    }


    public AddTravelTargetPacket(FriendlyByteBuf buf) {
        target = TravelRegistry.deserialize(buf.readNbt()).orElse(null);
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeNbt(target.save());
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == PlayNetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ClientHandler.handle(target);
    }

    public static class Handler extends Packet.PacketHandler<AddTravelTargetPacket> {

        @Override
        public AddTravelTargetPacket fromNetwork(FriendlyByteBuf buf) {
            return new AddTravelTargetPacket(buf);
        }

        @Override
        public void toNetwork(AddTravelTargetPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<INetworkDirection<?>> getDirection() {
            return Optional.of(PlayNetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static class ClientHandler {
        static void handle(ITravelTarget target) {
            TravelSavedData travelData = TravelSavedData.getTravelData(Minecraft.getInstance().level);
            travelData.addTravelTarget(Minecraft.getInstance().level, target);
        }
    }

}
