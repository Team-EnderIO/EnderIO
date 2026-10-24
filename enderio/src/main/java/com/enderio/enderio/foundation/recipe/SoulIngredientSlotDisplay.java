package com.enderio.enderio.foundation.recipe;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.ingredient.SoulIngredient;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.stream.Stream;

public record SoulIngredientSlotDisplay(SoulIngredient ingredient) implements SlotDisplay {
    public static final MapCodec<SoulIngredientSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            SoulIngredient.CODEC.fieldOf("ingredient").forGetter(SoulIngredientSlotDisplay::ingredient))
        .apply(i, SoulIngredientSlotDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulIngredientSlotDisplay> STREAM_CODEC = StreamCodec.composite(
        SoulIngredient.STREAM_CODEC,
        SoulIngredientSlotDisplay::ingredient,
        SoulIngredientSlotDisplay::new);

    public static final Type<SoulIngredientSlotDisplay> TYPE = new Type<>(CODEC, STREAM_CODEC);

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
        List<ItemStack> vials = ingredient().getEntityTypes().stream().map(t -> SoulVialItem.forSoul(Soul.of(t.value()))).toList();
        return factory instanceof DisplayContentsFactory.ForStacks<T> stacks ? vials.stream().map(stacks::forStack) : Stream.empty();
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return TYPE;
    }
}
