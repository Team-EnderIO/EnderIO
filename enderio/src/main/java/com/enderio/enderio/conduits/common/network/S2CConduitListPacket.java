package com.enderio.enderio.conduits.common.network;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.common.conduits.bundle.ConduitBundleBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record S2CConduitListPacket(int containerId, List<Holder<Conduit<?, ?>>> conduits)
        implements CustomPacketPayload {

    public static final Type<S2CConduitListPacket> TYPE = new Type<>(EnderIO.rl("conduit_list"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CConduitListPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, S2CConduitListPacket::containerId,
            Conduit.STREAM_CODEC.apply(ByteBufCodecs.list(ConduitBundleBlockEntity.MAX_CONDUITS)),
            S2CConduitListPacket::conduits, S2CConduitListPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
