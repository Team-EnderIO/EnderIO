package com.enderio.base.common.network;

import com.enderio.EnderIOBase;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.core.common.network.CustomMenuPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateCoordinateSelectionNameMenuPacket(int containerId, String name)
    implements CustomMenuPacketPayload<CoordinateMenu> {

    public static final Type<UpdateCoordinateSelectionNameMenuPacket> TYPE = new Type<>(EnderIOBase.loc("update_coordinate_selection_name"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCoordinateSelectionNameMenuPacket> STREAM_CODEC
        = StreamCodec.composite(
            ByteBufCodecs.INT,
            UpdateCoordinateSelectionNameMenuPacket::containerId,
            ByteBufCodecs.STRING_UTF8,
            UpdateCoordinateSelectionNameMenuPacket::name,
            UpdateCoordinateSelectionNameMenuPacket::new);

    @Override
    public Class<CoordinateMenu> menuClass() {
        return CoordinateMenu.class;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
