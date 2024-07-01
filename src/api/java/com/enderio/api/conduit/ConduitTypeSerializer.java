package com.enderio.api.conduit;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public abstract class ConduitTypeSerializer<T extends ConduitType<T, ?, ?>> {
    public abstract MapCodec<T> codec();

    protected static <T extends ConduitType<T, ?, ?>> Products.P2<RecordCodecBuilder.Mu<T>, ResourceLocation, Component>
    codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ConduitType::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(ConduitType::description)
        );
    }

    public static <T extends ConduitType<T, ?, ?>> ConduitTypeSerializer<T> makeSimple(BiFunction<ResourceLocation, Component, T> factory) {
        return new SimpleConduitTypeSerializer<>(factory);
    }

    private static class SimpleConduitTypeSerializer<T extends ConduitType<T, ?, ?>> extends
        ConduitTypeSerializer<T> {

        private final MapCodec<T> codec;

        public SimpleConduitTypeSerializer(BiFunction<ResourceLocation, Component, T> factory) {
            codec = RecordCodecBuilder.mapCodec(
                builder -> codecStart(builder)
                    .apply(builder, factory)
            );
        }

        @Override
        public MapCodec<T> codec() {
            return codec;
        }
    }
}
