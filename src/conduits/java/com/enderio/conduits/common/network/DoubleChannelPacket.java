package com.enderio.conduits.common.network;

import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.redstone.DoubleRedstoneChannel;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public record DoubleChannelPacket(ColorControl channel1, ColorControl channel2) implements Packet {

    public DoubleChannelPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(ColorControl.class), buf.readEnum(ColorControl.class));
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
                if (filter instanceof DoubleRedstoneChannel doubleRedstoneChannel) {
                    doubleRedstoneChannel.setChannels(channel1, channel2);
                }
            });
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeEnum(channel1);
        writeInto.writeEnum(channel2);
    }

    public static class Handler extends Packet.PacketHandler<DoubleChannelPacket> {

        @Override
        public DoubleChannelPacket fromNetwork(FriendlyByteBuf buf) {
            return new DoubleChannelPacket(buf);
        }

        @Override
        public void toNetwork(DoubleChannelPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
