package com.enderio.enderio.foundation.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.stream.Stream;

public record SizedSlotDisplay(SizedIngredient ingredient) implements SlotDisplay {
    public static final MapCodec<SizedSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        SizedIngredient.NESTED_CODEC.fieldOf("ingredient").forGetter(SizedSlotDisplay::ingredient))
        .apply(i, SizedSlotDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedSlotDisplay> STREAM_CODEC = StreamCodec.composite(
        SizedIngredient.STREAM_CODEC,
        SizedSlotDisplay::ingredient,
        SizedSlotDisplay::new);

    public static final Type<SizedSlotDisplay> TYPE = new Type<>(CODEC, STREAM_CODEC);

    @Override
    public <T> Stream<T> resolve(ContextMap contextMap, DisplayContentsFactory<T> factory) {
        return factory instanceof DisplayContentsFactory.ForStacks<T> stacks ?
            this.ingredient.ingredient().getValues().stream().map((i) -> stacks.forStack(new ItemStack(i, ingredient.count()))) : Stream.empty();
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return SlotDisplay.super.isEnabled(enabledFeatures);
    }
}
