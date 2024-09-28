package com.enderio.machines.mixin;

import com.enderio.EnderIOBase;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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
import java.util.Optional;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "lambda$apply$0", at = @At("TAIL"))
    private static void collectRecipe(ResourceLocation recipeId, ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType,
        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byName, WithConditions<Recipe<?>> recipeWithConditions, CallbackInfo ci) {
        if (recipeWithConditions.carrier() instanceof SmeltingRecipe smeltingRecipe) {
            Optional<RecipeHolder<AlloySmeltingRecipe>> convertedHolder = enderio$convert(recipeId, smeltingRecipe);

            convertedHolder.ifPresent(holder -> {
                byType.put(MachineRecipes.ALLOY_SMELTING.type().get(), holder);
                byName.put(holder.id(), holder);
            });
        }
    }

    @Unique
    private static Optional<RecipeHolder<AlloySmeltingRecipe>> enderio$convert(ResourceLocation originalId, SmeltingRecipe smeltingRecipe) {
        AbstractCookingRecipeAccessor accessor = (AbstractCookingRecipeAccessor) smeltingRecipe;

        if (accessor.getResult().isEmpty()) {
            EnderIOBase.LOGGER.warn("Unable to inherit the cooking recipe with ID: {}. Reason: The result item is empty.", originalId);
            return Optional.empty();
        }

        SizedIngredient input = new SizedIngredient(accessor.getIngredient(), 1);
        int energy = MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(List.of(input), accessor.getResult(), energy, accessor.getExperience(), true);

        String path = "smelting/" + originalId.getNamespace() + "/" + originalId.getPath();
        ResourceLocation id = EnderIOBase.loc(path);
        return Optional.of(new RecipeHolder<>(id, recipe));
    }
}
