package com.enderio.conduits.common.components;

import com.enderio.api.conduit.Conduit;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RepresentedConduitType(Holder<Conduit<?, ?, ?>> conduitType) {
    public static Codec<RepresentedConduitType> CODEC = Conduit.CODEC
        .xmap(RepresentedConduitType::new, RepresentedConduitType::conduitType);

    public static StreamCodec<RegistryFriendlyByteBuf, RepresentedConduitType> STREAM_CODEC = Conduit.STREAM_CODEC
        .map(RepresentedConduitType::new, RepresentedConduitType::conduitType);
}
