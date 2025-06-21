package com.enderio.machines.common.network;

import com.enderio.base.api.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record TransferItemsPacket(List<Ingredient> stacks, int startslot, int endslot, boolean maxTransfer) implements CustomPacketPayload {

    public static final Type<TransferItemsPacket> TYPE = new Type<>(EnderIO.loc("transfer_items"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransferItemsPacket> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
        TransferItemsPacket::stacks,
        ByteBufCodecs.INT,
        TransferItemsPacket::startslot,
        ByteBufCodecs.INT,
        TransferItemsPacket::endslot,
        ByteBufCodecs.BOOL,
        TransferItemsPacket::maxTransfer,
        TransferItemsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
