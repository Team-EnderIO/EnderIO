package com.enderio.enderio.api.armory.capacitor;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public record SocketedCapacitor(ItemStack capacitor) {
    public static final SocketedCapacitor EMPTY = new SocketedCapacitor(ItemStack.EMPTY);

    public static final Codec<SocketedCapacitor> CODEC = ItemStack.OPTIONAL_CODEC.xmap(SocketedCapacitor::new, SocketedCapacitor::capacitor);

    public static final StreamCodec<RegistryFriendlyByteBuf, SocketedCapacitor> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
        .map(SocketedCapacitor::new, SocketedCapacitor::capacitor);

    public SocketedCapacitor {
        capacitor = capacitor.copy();
    }

    /**
     * @return a copy of the socketed capacitor, or {@link ItemStack#EMPTY}.
     */
    @Override
    public ItemStack capacitor() {
        return capacitor.copy();
    }

    public boolean isEmpty() {
        return capacitor.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof SocketedCapacitor other && ItemStack.isSameItemSameComponents(capacitor, other.capacitor);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(capacitor);
    }
}
