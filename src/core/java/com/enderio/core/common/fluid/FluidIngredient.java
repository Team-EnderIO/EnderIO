package com.enderio.core.common.fluid;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FluidIngredient implements Predicate<Fluid> {
    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());

    private final Value[] values;
    @Nullable private Fluid[] fluids;

    protected FluidIngredient(Stream<? extends Value> stream) {
        this.values = stream.toArray(Value[]::new);
    }

    protected FluidIngredient(Value[] values) {
        this.values = values;
    }

    public Fluid[] getFluids() {
        if (this.fluids == null) {
            this.fluids = Arrays.stream(this.values).flatMap(value -> value.getFluids().stream()).distinct().toArray(Fluid[]::new);
        }

        return this.fluids;
    }

    @Override
    public boolean test(@Nullable Fluid other) {
        if (other == null) {
            return false;
        } else {
            for (Fluid fluid : this.getFluids()) {
                if (areFluidsEqual(fluid, other)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean areFluidsEqual(Fluid left, Fluid right) {
        return left.equals(right);
    }

    public boolean isEmpty() {
        return this.values.length == 0;
    }

    public static FluidIngredient fromValues(Stream<? extends Value> stream) {
        FluidIngredient ingredient = new FluidIngredient(stream);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static FluidIngredient of() {
        return EMPTY;
    }

    public static FluidIngredient of(Fluid... fluids) {
        return of(Arrays.stream(fluids));
    }

    public static FluidIngredient of(Stream<Fluid> fluids) {
        return fromValues(fluids.map(FluidValue::new));
    }

    public static FluidIngredient of(TagKey<Fluid> tag) {
        return fromValues(Stream.of(new TagValue(tag)));
    }

    public record FluidValue(Fluid fluid, BiFunction<Fluid, Fluid, Boolean> comparator) implements Value {
        public FluidValue(Fluid fluid) {
            this(fluid, FluidValue::areFluidsEqual);
        }

        static final Codec<FluidValue> CODEC = RecordCodecBuilder.create(p_311727_ -> p_311727_
            .group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidValue::fluid))
            .apply(p_311727_, FluidValue::new));

        @Override
        public boolean equals(Object other) {
            if (other instanceof FluidValue otherFluid) {
                return comparator().apply(fluid(), otherFluid.fluid());
            }
            return false;
        }

        @Override
        public Collection<Fluid> getFluids() {
            return Collections.singleton(this.fluid);
        }

        private static boolean areFluidsEqual(Fluid left, Fluid right) {
            return left.equals(right);
        }
    }

    public record TagValue(TagKey<Fluid> tag) implements Value {
        static final Codec<TagValue> CODEC = RecordCodecBuilder.create(
            p_301118_ -> p_301118_.group(TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(p_301154_ -> p_301154_.tag)).apply(p_301118_, TagValue::new));

        @Override
        public boolean equals(Object other) {
            return other instanceof TagValue otherTag && otherTag.tag.location().equals(this.tag.location());
        }

        @Override
        public Collection<Fluid> getFluids() {
            List<Fluid> list = Lists.newArrayList();

            for (Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(this.tag)) {
                list.add(holder.value());
            }

            return list;
        }
    }

    protected interface Value {
        Codec<Value> CODEC = ExtraCodecs
            .xor(FluidValue.CODEC, TagValue.CODEC)
            .xmap(p_300956_ -> p_300956_.map(p_300932_ -> p_300932_, p_301313_ -> p_301313_), p_301304_ -> {
                if (p_301304_ instanceof TagValue fluidTag) {
                    return Either.right(fluidTag);
                } else if (p_301304_ instanceof FluidValue fluidValue) {
                    return Either.left(fluidValue);
                } else {
                    throw new UnsupportedOperationException("This is neither an fluid value nor a tag value.");
                }
            });

        Collection<Fluid> getFluids();
    }
}
