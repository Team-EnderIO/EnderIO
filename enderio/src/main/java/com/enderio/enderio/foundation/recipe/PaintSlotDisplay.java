package com.enderio.enderio.foundation.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.stream.Stream;

public class PaintSlotDisplay implements SlotDisplay {
    public static final PaintSlotDisplay INSTANCE = new PaintSlotDisplay();
    public static final MapCodec<PaintSlotDisplay> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, PaintSlotDisplay> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<PaintSlotDisplay> TYPE = new Type<>(CODEC, STREAM_CODEC);

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> builder) {
        var paint = PaintIngredient.of().items().map(h -> new ItemStack(h.value())).filter(s-> PaintIngredient.of().test(s));
        return builder instanceof DisplayContentsFactory.ForStacks<T> stacks ? paint.map(stacks::forStack) : Stream.empty();
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return TYPE;
    }
}
