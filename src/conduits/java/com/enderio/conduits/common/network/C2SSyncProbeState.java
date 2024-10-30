package com.enderio.conduits.common.network;

import com.enderio.conduits.common.items.ConduitProbeItem;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public class C2SSyncProbeState implements Packet {
    private ConduitProbeItem.State state;

    public C2SSyncProbeState(ConduitProbeItem.State state) {
        this.state = state;
    }
    
    public C2SSyncProbeState(FriendlyByteBuf buf) {
        this.state = buf.readEnum(ConduitProbeItem.State.class);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getSender() != null;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ItemStack probeStack;
            if (context.getSender().getMainHandItem().getItem() instanceof ConduitProbeItem) {
                probeStack = context.getSender().getMainHandItem();
            } else {
                probeStack = context.getSender().getOffhandItem();
            }
            ConduitProbeItem.setState(probeStack, state, false);
            context.getSender().sendSystemMessage(Component.literal("Changed probe to " + state));
        });
        context.setPacketHandled(true);
    }
    
    protected void write(FriendlyByteBuf buf) {
        buf.writeEnum(state);
    }

    public static class Handler extends PacketHandler<C2SSyncProbeState> {

        @Override
        public C2SSyncProbeState fromNetwork(FriendlyByteBuf buf) {
            return new C2SSyncProbeState(buf);
        }

        @Override
        public void toNetwork(C2SSyncProbeState packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
