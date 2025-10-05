package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record ServerboundSetItemFilterSlotPacket(int containerId, int slotIndex, ItemStack itemStack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSetItemFilterSlotPacket> TYPE = new CustomPacketPayload.Type<>(
        EnderIO.rl("set_item_filter_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetItemFilterSlotPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundSetItemFilterSlotPacket::containerId, ByteBufCodecs.INT, ServerboundSetItemFilterSlotPacket::slotIndex,
            ItemStack.STREAM_CODEC, ServerboundSetItemFilterSlotPacket::itemStack, ServerboundSetItemFilterSlotPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
