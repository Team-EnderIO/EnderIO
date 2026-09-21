package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record ServerboundSetGhostSlotPacket(int containerId, int slotIndex, ItemStack itemStack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSetGhostSlotPacket> TYPE = new CustomPacketPayload.Type<>(
        EnderIO.rl("set_ghost_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetGhostSlotPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ServerboundSetGhostSlotPacket::containerId, ByteBufCodecs.INT, ServerboundSetGhostSlotPacket::slotIndex,
        ItemStack.STREAM_CODEC, ServerboundSetGhostSlotPacket::itemStack, ServerboundSetGhostSlotPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
