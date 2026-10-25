package com.enderio.enderio.foundation.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

// Component for saving to machine items
public record MachineCraftingState(
    ResourceKey<Recipe<?>> recipeId,
    int craftingTicks,
    long randomSeed
) {
    public static final Codec<MachineCraftingState> CODEC = RecordCodecBuilder.create(inst ->
        inst.group(
            ResourceKey.codec(Registries.RECIPE).fieldOf("RecipeId").forGetter(MachineCraftingState::recipeId),
            Codec.INT.fieldOf("CraftingTicks").forGetter(MachineCraftingState::craftingTicks),
            Codec.LONG.fieldOf("RandomSeed").forGetter(MachineCraftingState::randomSeed)
        ).apply(inst, MachineCraftingState::new));
}
