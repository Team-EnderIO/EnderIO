package com.enderio.conduits.common.components;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RepresentedConduitType(ConduitType<?, ?, ?> conduitType) {
    public static Codec<RepresentedConduitType> CODEC = EnderIORegistries.CONDUIT_TYPES.byNameCodec()
        .xmap(RepresentedConduitType::new, RepresentedConduitType::conduitType);

    public static StreamCodec<RegistryFriendlyByteBuf, RepresentedConduitType> STREAM_CODEC = ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_TYPES)
        .map(RepresentedConduitType::new, RepresentedConduitType::conduitType);
}
