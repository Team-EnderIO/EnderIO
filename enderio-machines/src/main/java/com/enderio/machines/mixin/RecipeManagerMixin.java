package com.enderio.machines.mixin;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeManager.class)
public abstract class RecipeManagerMixin {

    private static Logger LOGGER;

    @Inject(method = "lambda$apply$0", at = @At("TAIL"))
    private static void collectRecipe(ResourceLocation recipeId,
            ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType,
            ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byName,
            WithConditions<Recipe<?>> recipeWithConditions, CallbackInfo ci) {
        if (recipeWithConditions.carrier() instanceof SmeltingRecipe smeltingRecipe) {

            enderio$convertSmeltingRecipe(recipeId, smeltingRecipe).ifPresent(convertedHolder -> {
                byType.put(MachineRecipes.ALLOY_SMELTING.type().get(), convertedHolder);
                byName.put(convertedHolder.id(), convertedHolder);
            });
        }
    }

    @Unique
    private static Optional<RecipeHolder<AlloySmeltingRecipe>> enderio$convertSmeltingRecipe(
            ResourceLocation originalId, SmeltingRecipe smeltingRecipe) {
        AbstractCookingRecipeAccessor accessor = (AbstractCookingRecipeAccessor) smeltingRecipe;

        if (accessor.getResult().isEmpty()) {
            LOGGER.warn("[EnderIO] Unable to inherit the cooking recipe with ID: {}. Reason: The result item is empty.",
                    originalId);
            return Optional.empty();
        }

        SizedIngredient input = new SizedIngredient(accessor.getIngredient(), 1);
        int energy = MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(List.of(input), accessor.getResult(), energy,
                accessor.getExperience(), true);

        String path = "smelting/" + originalId.getNamespace() + "/" + originalId.getPath();
        ResourceLocation id = EnderIO.loc(path);
        return Optional.of(new RecipeHolder<>(id, recipe));
    }
}
