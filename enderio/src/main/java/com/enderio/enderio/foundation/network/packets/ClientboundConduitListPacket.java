package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record ClientboundConduitListPacket(int containerId, List<Holder<Conduit<?, ?>>> conduits)
        implements CustomPacketPayload {

    public static final Type<ClientboundConduitListPacket> TYPE = new Type<>(EnderIO.id("conduit_list"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundConduitListPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundConduitListPacket::containerId,
            Conduit.STREAM_CODEC.apply(ByteBufCodecs.list(ConduitBundleBlockEntity.MAX_CONDUITS)),
            ClientboundConduitListPacket::conduits, ClientboundConduitListPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
