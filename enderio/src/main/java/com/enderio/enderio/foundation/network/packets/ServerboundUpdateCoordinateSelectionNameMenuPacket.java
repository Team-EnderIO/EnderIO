package com.enderio.enderio.foundation.network.packets;

import com.enderio.core.common.network.CustomMenuPacketPayload;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.tools.coordinate_selector.CoordinateMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundUpdateCoordinateSelectionNameMenuPacket(int containerId, String name)
    implements CustomMenuPacketPayload<CoordinateMenu> {

    public static final Type<ServerboundUpdateCoordinateSelectionNameMenuPacket> TYPE = new Type<>(EnderIO.id("update_coordinate_selection_name"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUpdateCoordinateSelectionNameMenuPacket> STREAM_CODEC
        = StreamCodec.composite(
            ByteBufCodecs.INT,
            ServerboundUpdateCoordinateSelectionNameMenuPacket::containerId,
            ByteBufCodecs.STRING_UTF8,
            ServerboundUpdateCoordinateSelectionNameMenuPacket::name,
            ServerboundUpdateCoordinateSelectionNameMenuPacket::new);

    @Override
    public Class<CoordinateMenu> menuClass() {
        return CoordinateMenu.class;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
