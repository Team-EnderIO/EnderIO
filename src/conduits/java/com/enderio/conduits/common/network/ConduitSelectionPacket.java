package com.enderio.conduits.common.network;

import com.enderio.conduits.common.init.EIOConduitTypes;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public record ConduitSelectionPacket(int type) implements Packet {

    public ConduitSelectionPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getSender() != null;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if (context.getSender().containerMenu instanceof ConduitMenu menu) {
            menu.setConduitType(EIOConduitTypes.getById(this.type));
        }
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeInt(this.type);
    }

    public static class Handler extends Packet.PacketHandler<ConduitSelectionPacket> {

        @Override
        public ConduitSelectionPacket fromNetwork(FriendlyByteBuf buf) {
            return new ConduitSelectionPacket(buf);
        }

        @Override
        public void toNetwork(ConduitSelectionPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
