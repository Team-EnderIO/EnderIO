package com.enderio.base.common.network;

import com.enderio.core.common.menu.FilterSlot;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public record C2SSetItemFilterSlot(int containerId, int slotIndex, ItemStack itemStack) implements Packet {
    public C2SSetItemFilterSlot(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readItem());
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        if (context.getSender() == null) {
            return false;
        }

        var menu = context.getSender().containerMenu;
        if (menu == null || menu.containerId != containerId || slotIndex >= menu.slots.size()) {
            return false;
        }

        return true;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if (context.getSender().containerMenu.getSlot(slotIndex) instanceof FilterSlot<?> filterSlot) {
            filterSlot.safeInsert(itemStack);
        }
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeInt(containerId);
        writeInto.writeInt(slotIndex);
        writeInto.writeItem(itemStack);
    }

    public static class Handler extends Packet.PacketHandler<C2SSetItemFilterSlot> {

        @Override
        public C2SSetItemFilterSlot fromNetwork(FriendlyByteBuf buf) {
            return new C2SSetItemFilterSlot(buf);
        }

        @Override
        public void toNetwork(C2SSetItemFilterSlot packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
