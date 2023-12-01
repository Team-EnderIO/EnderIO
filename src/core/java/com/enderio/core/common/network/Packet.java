package com.enderio.core.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.INetworkDirection;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;

public interface Packet {

    boolean isValid(NetworkEvent.Context context);

    void handle(NetworkEvent.Context context);


    abstract class PacketHandler<MSG extends Packet> {
        public abstract MSG fromNetwork(FriendlyByteBuf buf);

        public abstract void toNetwork(MSG packet, FriendlyByteBuf buf);

        public void handle(MSG msg, NetworkEvent.Context context) {
            if (msg.isValid(context)) {
                context.enqueueWork(() -> msg.handle(context));
            } else {
                logPacketError(context, "didn't pass check and is invalid", msg);
            }
            context.setPacketHandled(true);
        }

        public abstract Optional<INetworkDirection<?>> getDirection();
    }

    static void logPacketError(NetworkEvent.Context context, String error, Packet packet) {
        String sender;
        if (context.getDirection() == PlayNetworkDirection.PLAY_TO_CLIENT) {
            sender = "the server";
        } else {
            sender = context.getSender().getName().getContents() + " with IP-Address " + context.getSender().getIpAddress();
        }
        LogManager.getLogger().warn("Packet {} from {}: {}", packet.getClass(),sender, error);
    }
}
