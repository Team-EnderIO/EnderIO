package com.enderio.core.common.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public record NamedFluidContents(ImmutableMap<String, FluidStack> fluidMap) {
    public static Codec<NamedFluidContents> CODEC = Codec.unboundedMap(Codec.STRING, FluidStack.CODEC)
            .xmap(contents -> new NamedFluidContents(ImmutableMap.copyOf(contents)), NamedFluidContents::fluidMap);

    // @formatter:off
    public static StreamCodec<RegistryFriendlyByteBuf, NamedFluidContents> STREAM_CODEC = ByteBufCodecs.map(
        HashMap::new, ByteBufCodecs.STRING_UTF8, FluidStack.STREAM_CODEC)
        .map(contents -> new NamedFluidContents(ImmutableMap.copyOf(contents)), i -> new HashMap<>(i.fluidMap()));
    // @formatter:on

    public static NamedFluidContents copyOf(Map<String, FluidStack> fluidMap) {
        var copies = fluidMap.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, i -> i.getValue().copy()));

        return new NamedFluidContents(ImmutableMap.copyOf(copies));
    }

    public FluidStack copy(String key) {
        return fluidMap.getOrDefault(key, FluidStack.EMPTY).copy();
    }
}
