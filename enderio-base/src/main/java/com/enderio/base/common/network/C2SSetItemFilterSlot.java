package com.enderio.base.common.network;

import com.enderio.base.api.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record C2SSetItemFilterSlot(int containerId, int slotIndex, ItemStack itemStack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SSetItemFilterSlot> TYPE = new CustomPacketPayload.Type<>(
        EnderIO.loc("set_item_filter_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSetItemFilterSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, C2SSetItemFilterSlot::containerId, ByteBufCodecs.INT, C2SSetItemFilterSlot::slotIndex,
            ItemStack.STREAM_CODEC, C2SSetItemFilterSlot::itemStack, C2SSetItemFilterSlot::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
