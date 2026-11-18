package com.enderio.enderio.api.armory.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record DarkSteelToggle(Component name, boolean defaultState, Optional<ResourceLocation> key) {
    public static final Codec<DarkSteelToggle> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(DarkSteelToggle::name),
            Codec.BOOL.optionalFieldOf("default", true).forGetter(DarkSteelToggle::defaultState),
            ResourceLocation.CODEC.optionalFieldOf("key").forGetter(DarkSteelToggle::key)
        ).apply(inst, DarkSteelToggle::new));
}
