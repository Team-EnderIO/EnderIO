package com.enderio.core.common.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record NamedFluidContents(ImmutableMap<String, FluidStack> fluidMap) {
    public static Codec<NamedFluidContents> CODEC = Codec.unboundedMap(Codec.STRING, FluidStack.CODEC)
        .xmap(contents -> new NamedFluidContents(ImmutableMap.copyOf(contents)), NamedFluidContents::fluidMap);

    public static StreamCodec<RegistryFriendlyByteBuf, NamedFluidContents> STREAM_CODEC = ByteBufCodecs.map(
        HashMap::new, ByteBufCodecs.STRING_UTF8, FluidStack.STREAM_CODEC)
        .map(contents -> new NamedFluidContents(ImmutableMap.copyOf(contents)), i -> new HashMap<>(i.fluidMap()));

    public static NamedFluidContents copyOf(Map<String, FluidStack> fluidMap) {
        var copies = fluidMap.entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                i -> i.getValue().copy()));

        return new NamedFluidContents(ImmutableMap.copyOf(copies));
    }

    public FluidStack copy(String key) {
        return fluidMap.getOrDefault(key, FluidStack.EMPTY).copy();
    }
}
