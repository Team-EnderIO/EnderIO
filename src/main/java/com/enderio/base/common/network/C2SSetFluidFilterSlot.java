package com.enderio.base.common.network;

import com.enderio.core.common.menu.FluidFilterSlot;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public record C2SSetFluidFilterSlot(int containerId, int slotIndex, FluidStack fluidStack) implements Packet {
    public C2SSetFluidFilterSlot(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readFluidStack());
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
        if (context.getSender().containerMenu.getSlot(slotIndex) instanceof FluidFilterSlot filterSlot) {
            filterSlot.setResource(fluidStack);
        }
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeInt(containerId);
        writeInto.writeInt(slotIndex);
        writeInto.writeFluidStack(fluidStack);
    }

    public static class Handler extends PacketHandler<C2SSetFluidFilterSlot> {

        @Override
        public C2SSetFluidFilterSlot fromNetwork(FriendlyByteBuf buf) {
            return new C2SSetFluidFilterSlot(buf);
        }

        @Override
        public void toNetwork(C2SSetFluidFilterSlot packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
