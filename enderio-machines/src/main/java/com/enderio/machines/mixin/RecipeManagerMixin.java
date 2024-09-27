package com.enderio.machines.mixin;

import com.enderio.EnderIOBase;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "lambda$apply$0", at = @At("TAIL"))
    private static void collectRecipe(ResourceLocation recipeId, ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType,
        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byName, WithConditions<Recipe<?>> recipeWithConditions, CallbackInfo ci) {
        if (recipeWithConditions.carrier() instanceof SmeltingRecipe smeltingRecipe) {
            RecipeHolder<AlloySmeltingRecipe> convertedHolder = enderio$convert(recipeId, smeltingRecipe);
            byType.put(MachineRecipes.ALLOY_SMELTING.type().get(), convertedHolder);
            byName.put(convertedHolder.id(), convertedHolder);
        }
    }

    @Unique
    private static RecipeHolder<AlloySmeltingRecipe> enderio$convert(ResourceLocation originalId, SmeltingRecipe smeltingRecipe) {
        AbstractCookingRecipeAccessor accessor = (AbstractCookingRecipeAccessor) smeltingRecipe;
        SizedIngredient input = new SizedIngredient(accessor.getIngredient(), 1);
        int energy = MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(List.of(input), accessor.getResult(), energy, accessor.getExperience(), true);

        String path = "smelting/" + originalId.getNamespace() + "/" + originalId.getPath();
        ResourceLocation id = EnderIOBase.loc(path);
        return new RecipeHolder<>(id, recipe);
    }
}
