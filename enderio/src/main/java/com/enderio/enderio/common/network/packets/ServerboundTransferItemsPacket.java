package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record ServerboundTransferItemsPacket(List<Ingredient> stacks, int startslot, int endslot, boolean maxTransfer) implements CustomPacketPayload {

    public static final Type<ServerboundTransferItemsPacket> TYPE = new Type<>(EnderIO.rl("transfer_items"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTransferItemsPacket> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
        ServerboundTransferItemsPacket::stacks,
        ByteBufCodecs.INT,
        ServerboundTransferItemsPacket::startslot,
        ByteBufCodecs.INT,
        ServerboundTransferItemsPacket::endslot,
        ByteBufCodecs.BOOL,
        ServerboundTransferItemsPacket::maxTransfer,
        ServerboundTransferItemsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
