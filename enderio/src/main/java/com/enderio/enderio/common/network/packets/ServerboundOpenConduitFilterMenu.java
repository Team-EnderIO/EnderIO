package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.common.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundOpenConduitFilterMenu(int containerId, int slot) implements CustomPacketPayload {

    public static final Type<ServerboundOpenConduitFilterMenu> TYPE = new Type<>(EnderIO.rl("client_open_conduit_filter_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOpenConduitFilterMenu> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundOpenConduitFilterMenu::containerId, ByteBufCodecs.INT, ServerboundOpenConduitFilterMenu::slot,
            ServerboundOpenConduitFilterMenu::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
