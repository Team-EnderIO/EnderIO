package com.enderio.conduits.common.network;

import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.redstone.RedstoneCountFilter;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public record CountFilterPacket(ColorControl channel, int maxCount, int count, boolean active) implements Packet {

    public CountFilterPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(ColorControl.class), buf.readInt(), buf.readInt(), buf.readBoolean());
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
                if (filter instanceof RedstoneCountFilter countFilter) {
                    countFilter.setState(this);
                }
            });
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeEnum(channel);
        writeInto.writeInt(maxCount);
        writeInto.writeInt(count);
        writeInto.writeBoolean(active);
    }

    public static class Handler extends Packet.PacketHandler<CountFilterPacket> {

        @Override
        public CountFilterPacket fromNetwork(FriendlyByteBuf buf) {
            return new CountFilterPacket(buf);
        }

        @Override
        public void toNetwork(CountFilterPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
