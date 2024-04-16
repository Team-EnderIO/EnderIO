package com.enderio.conduits.common.network;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.menu.ConduitFilterMenu;
import com.enderio.core.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.Optional;

public class ConduitItemFilterPacket implements Packet {
    public final ItemStack theFilter;
    public boolean ignoreMode;
    public boolean strictMode;

    public ConduitItemFilterPacket(ItemStack theFilter, boolean ignoreMode, boolean strictMode) {
        this.theFilter = theFilter;
        this.ignoreMode = ignoreMode;
        this.strictMode = strictMode;
    }

    public ConduitItemFilterPacket(FriendlyByteBuf buf) {
        theFilter = buf.readItem();
        ignoreMode = buf.readBoolean();
        strictMode = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeItem(theFilter);
        buf.writeBoolean(ignoreMode);
        buf.writeBoolean(strictMode);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getSender() != null
            && !theFilter.isEmpty()
            && (theFilter.is(ConduitItems.BASIC_ITEM_FILTER.asItem())
                || theFilter.is(ConduitItems.ADVANCED_ITEM_FILTER.asItem())
                || theFilter.is(ConduitItems.BIG_ITEM_FILTER.asItem())
                || theFilter.is(ConduitItems.BIG_ADVANCED_ITEM_FILTER.asItem()))
            && (context.getSender().containerMenu instanceof ConduitFilterMenu cfm)
            && ItemStack.isSameItem(theFilter, cfm.inventory.filter);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if (Objects.requireNonNull(context.getSender()).containerMenu instanceof ConduitFilterMenu cfm) {
            var cap = cfm.inventory.filter.getCapability(EIOCapabilities.ITEM_FILTER).resolve().get();
            cap.setIgnoreMode(ignoreMode);
            cap.setStrictMode(strictMode);
        }
    }

    public static class Handler extends Packet.PacketHandler<ConduitItemFilterPacket> {
        @Override
        public ConduitItemFilterPacket fromNetwork(FriendlyByteBuf buf) {
            return new ConduitItemFilterPacket(buf);
        }

        @Override
        public void toNetwork(ConduitItemFilterPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
