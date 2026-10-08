package com.enderio.enderio.foundation.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record SolarSoul(boolean operatesInDaytime, boolean operatesInNighttime, Optional<ResourceKey<Level>> dimensionOverride) {
    public static final Codec<SolarSoul> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.BOOL.fieldOf("operatesInDaytime").forGetter(SolarSoul::operatesInDaytime),
            Codec.BOOL.fieldOf("operatesInNighttime").forGetter(SolarSoul::operatesInNighttime),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimensionOverride").forGetter(SolarSoul::dimensionOverride)
        ).apply(instance, SolarSoul::new));
}
