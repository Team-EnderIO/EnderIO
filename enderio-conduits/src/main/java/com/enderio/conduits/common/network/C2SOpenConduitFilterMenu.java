package com.enderio.conduits.common.network;

import com.enderio.base.api.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SOpenConduitFilterMenu(int containerId, int slot) implements CustomPacketPayload {

    public static final Type<C2SOpenConduitFilterMenu> TYPE = new Type<>(EnderIO.loc("client_open_conduit_filter_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SOpenConduitFilterMenu> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, C2SOpenConduitFilterMenu::containerId, ByteBufCodecs.INT, C2SOpenConduitFilterMenu::slot,
            C2SOpenConduitFilterMenu::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
