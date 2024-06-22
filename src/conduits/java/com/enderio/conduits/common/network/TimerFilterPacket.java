package com.enderio.conduits.common.network;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.redstone.RedstoneTimerFilter;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public record TimerFilterPacket(int ticks, int maxTicks) implements Packet {

    public TimerFilterPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getSender() != null;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ItemStack mainHandItem = context.getSender().getMainHandItem();
        mainHandItem.getCapability(EIOCapabilities.FILTER)
            .ifPresent(filter -> {
                if (filter instanceof RedstoneTimerFilter timerFilter) {
                    timerFilter.setTimer(ticks, maxTicks);
                }
            });
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeInt(ticks);
        writeInto.writeInt(maxTicks);
    }

    public static class Handler extends Packet.PacketHandler<TimerFilterPacket> {

        @Override
        public TimerFilterPacket fromNetwork(FriendlyByteBuf buf) {
            return new TimerFilterPacket(buf);
        }

        @Override
        public void toNetwork(TimerFilterPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
