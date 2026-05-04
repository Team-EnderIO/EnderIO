package com.enderio.enderio.api.recipes.alloy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record AlloySmeltingRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation, int operationTime) implements RecipeDisplay {
    public static final MapCodec<AlloySmeltingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(
                SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(AlloySmeltingRecipeDisplay::ingredients),
                SlotDisplay.CODEC.fieldOf("result").forGetter(AlloySmeltingRecipeDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AlloySmeltingRecipeDisplay::craftingStation),
                Codec.INT.fieldOf("operation_time").forGetter(AlloySmeltingRecipeDisplay::operationTime)
            )
            .apply(inst, AlloySmeltingRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
        SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
        AlloySmeltingRecipeDisplay::ingredients,
        SlotDisplay.STREAM_CODEC,
        AlloySmeltingRecipeDisplay::result,
        SlotDisplay.STREAM_CODEC,
        AlloySmeltingRecipeDisplay::craftingStation,
        ByteBufCodecs.INT,
        AlloySmeltingRecipeDisplay::operationTime,
        AlloySmeltingRecipeDisplay::new
    );

    // TODO: we need to register this.
    public static final RecipeDisplay.Type<AlloySmeltingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeDisplay.Type<? extends RecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet flagSet) {
        return this.ingredients.stream().allMatch(i -> i.isEnabled(flagSet)) && RecipeDisplay.super.isEnabled(flagSet);
    }
}
