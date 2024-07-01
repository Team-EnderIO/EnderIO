package com.enderio.conduits.common.components;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RepresentedConduitType(Holder<ConduitType<?, ?, ?>> conduitType) {
    public static Codec<RepresentedConduitType> CODEC = ConduitType.CODEC
        .xmap(RepresentedConduitType::new, RepresentedConduitType::conduitType);

    public static StreamCodec<RegistryFriendlyByteBuf, RepresentedConduitType> STREAM_CODEC = ConduitType.STREAM_CODEC
        .map(RepresentedConduitType::new, RepresentedConduitType::conduitType);
}
