package com.enderio.core.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackContentsFactory;
import net.neoforged.neoforge.fluids.crafting.display.ForFluidStacks;

import java.util.Objects;
import java.util.stream.Stream;

public record WithCountSlotDisplay(SlotDisplay source, int count) implements SlotDisplay {

    public static final MapCodec<WithCountSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i
        .group(SlotDisplay.CODEC.fieldOf("contents").forGetter(WithCountSlotDisplay::source),
            Codec.INT.fieldOf("count").forGetter(WithCountSlotDisplay::count))
        .apply(i, WithCountSlotDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WithCountSlotDisplay> STREAM_CODEC = StreamCodec.composite(
        SlotDisplay.STREAM_CODEC,
        WithCountSlotDisplay::source,
        ByteBufCodecs.INT,
        WithCountSlotDisplay::count,
        WithCountSlotDisplay::new
    );

    public static final Type<WithCountSlotDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

    public WithCountSlotDisplay(SizedIngredient sizedIngredient) {
        this(sizedIngredient.ingredient().display(), sizedIngredient.count());
    }

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> builder) {
        Stream<T> results;
        if (builder instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            var resolved = this.source.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE);
            Objects.requireNonNull(stacks);
            results = resolved.map(stack -> stacks.forStack(stack.copyWithCount(count)));
        } else if (builder instanceof ForFluidStacks<T> fluidStacks) {
            var resolved = this.source.resolve(context, FluidStackContentsFactory.INSTANCE);
            Objects.requireNonNull(fluidStacks);
            results = resolved.map(stack -> fluidStacks.forStack(stack.copyWithAmount(count)));
        } else {
            results = Stream.empty();
        }

        return results;
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return TYPE;
    }
}
