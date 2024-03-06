package com.enderio.core.common.recipes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FluidIngredient implements Predicate<Fluid> {
    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
    private static final Codec<Value[]> CODEC_LIST = Codec
        .list(Value.CODEC)
        .comapFlatMap(list -> list.isEmpty() ?
            DataResult.error(() -> "Fluid array cannot be empty, at least one fluid must be defined") :
            DataResult.success(list.toArray(new Value[0])), List::of);

    public static final Codec<FluidIngredient> CODEC = ExtraCodecs
        .either(CODEC_LIST, Value.CODEC)
        .flatComapMap(either -> either.map(FluidIngredient::new, value -> new FluidIngredient(new Value[] { value })), fluidIngredient -> {
            if (fluidIngredient.values.length == 1) {
                return DataResult.success(Either.right(fluidIngredient.values[0]));
            } else {
                return fluidIngredient.values.length == 0 ?
                    DataResult.error(() -> "Fluid array cannot be empty, at least one fluid must be defined") :
                    DataResult.success(Either.left(fluidIngredient.values));
            }
        });

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

    public final void toNetwork(FriendlyByteBuf pBuffer) {
        BiConsumer<FriendlyByteBuf, Fluid> writer = (buf, fluid) -> buf.writeId(BuiltInRegistries.FLUID, fluid);
        pBuffer.writeCollection(Arrays.asList(this.getFluids()), writer::accept);
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buf) {
        var size = buf.readVarInt();
        return new FluidIngredient(Stream.generate(() -> new FluidValue(buf.readById(BuiltInRegistries.FLUID))).limit(size));
    }

    public record FluidValue(Fluid fluid, BiFunction<Fluid, Fluid, Boolean> comparator) implements Value {
        public FluidValue(Fluid fluid) {
            this(fluid, FluidValue::areFluidsEqual);
        }

        static final Codec<FluidValue> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid_name").forGetter(FluidValue::fluid))
            .apply(instance, FluidValue::new));

        @Override
        public boolean equals(Object other) {
            if (other instanceof FluidValue otherFluid) {
                return comparator().apply(fluid(), otherFluid.fluid());
            }
            return false;
        }

        @Override
        public Codec<FluidValue> getCodec() {
            return CODEC;
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
            instance -> instance.group(TagKey.codec(Registries.FLUID).fieldOf("fluid_tag").forGetter(TagValue::tag)).apply(instance, TagValue::new));

        @Override
        public boolean equals(Object other) {
            return other instanceof TagValue otherTag && otherTag.tag.location().equals(this.tag.location());
        }

        @Override
        public Codec<TagValue> getCodec() {
            return CODEC;
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

    protected sealed interface Value {
        Codec<Value> CODEC = ExtraCodecs
            .xor(FluidValue.CODEC, TagValue.CODEC).xmap(either -> either.map(fluidValue -> fluidValue, tagValue -> tagValue), value -> {
                if (value instanceof TagValue tagValue) {
                    return Either.right(tagValue);
                } else if (value instanceof FluidValue fluidValue) {
                    return Either.left(fluidValue);
                } else {
                    throw new UnsupportedOperationException("This is neither an fluid value nor a tag value.");
                }
            });

        Codec<? extends Value> getCodec();
        Collection<Fluid> getFluids();
    }
}
