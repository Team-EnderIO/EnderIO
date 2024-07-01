package com.enderio.api.conduit;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public record NewConduitTypeSerializer<T extends ConduitType<T, ?, ?>>(MapCodec<T> codec) {

    public static <T extends ConduitType<T, ?, ?>> NewConduitTypeSerializer<T> of(BiFunction<ResourceLocation, Component, T> factory) {
        return new NewConduitTypeSerializer<T>(RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ConduitType::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(ConduitType::description)
            ).apply(builder, factory)
        ));
    }
}
