package com.enderio.machines.common.network;

import com.enderio.core.common.network.Packet;
import com.enderio.machines.common.menu.XPObeliskMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;

public class PlayerExperiencePacket implements Packet {

    public final UUID playerID;
    public final int experience;
    public PlayerExperiencePacket(UUID playerID, int experience){
        this.playerID = playerID;
        this.experience = experience;
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getSender() != null;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if(context.getSender().containerMenu instanceof XPObeliskMenu){
            Player player = context.getSender().serverLevel().getPlayerByUUID(this.playerID);
            if(player != null) {
                player.giveExperiencePoints(experience);
            }
        }
    }

    public static class Handler extends Packet.PacketHandler<PlayerExperiencePacket> {

        @Override
        public PlayerExperiencePacket fromNetwork(FriendlyByteBuf buf) {

            UUID playerID = buf.readUUID();
            int exp = buf.readInt();
            return new PlayerExperiencePacket(playerID, exp);
        }

        @Override
        public void toNetwork(PlayerExperiencePacket packet, FriendlyByteBuf buf) {
            buf.writeUUID(packet.playerID);
            buf.writeInt(packet.experience);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.empty();
        }
    }

}
