package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.poi.EnderPOI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

public record ClientboundEnderPOIUpdatedPacket(@Nullable EnderPOI target) implements CustomPacketPayload {

    public static final Type<ClientboundEnderPOIUpdatedPacket> TYPE = new Type<>(EnderIO.rl("add_ender_poi"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEnderPOIUpdatedPacket> STREAM_CODEC = EnderPOI.STREAM_CODEC
        .map(ClientboundEnderPOIUpdatedPacket::new, ClientboundEnderPOIUpdatedPacket::target);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
