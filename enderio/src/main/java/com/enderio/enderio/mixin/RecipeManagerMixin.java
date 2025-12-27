package com.enderio.enderio.mixin;

import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RecipeManager.class)
public abstract class RecipeManagerMixin {

    private static Logger LOGGER;

    //TODO See if we still need this for kubejs
//    @Inject(method = "lambda$apply$0", at = @At("TAIL"))
//    private static void collectRecipe(Identifier recipeId,
//            ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType,
//            ImmutableMap.Builder<Identifier, RecipeHolder<?>> byName,
//            WithConditions<Recipe<?>> recipeWithConditions, CallbackInfo ci) {
//        if (recipeWithConditions.carrier() instanceof SmeltingRecipe smeltingRecipe) {
//
//            enderio$convertSmeltingRecipe(recipeId, smeltingRecipe).ifPresent(convertedHolder -> {
//                byType.put(EIORecipes.ALLOY_SMELTING.type().get(), convertedHolder);
//                byName.put(convertedHolder.id(), convertedHolder);
//            });
//        }
//    }
//
//    @Unique
//    private static Optional<RecipeHolder<AlloySmeltingRecipe>> enderio$convertSmeltingRecipe(
//            Identifier originalId, SmeltingRecipe smeltingRecipe) {
//        AbstractCookingRecipeAccessor accessor = (AbstractCookingRecipeAccessor) smeltingRecipe;
//
//        if (accessor.getResult().isEmpty()) {
//            LOGGER.warn("[EnderIO] Unable to inherit the cooking recipe with ID: {}. Reason: The result item is empty.",
//                    originalId);
//            return Optional.empty();
//        }
//
//        SizedIngredient input = new SizedIngredient(accessor.getIngredient(), 1);
//        int energy = MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
//        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(List.of(input), accessor.getResult(), energy,
//                accessor.getExperience(), true);
//
//        String path = "smelting/" + originalId.getNamespace() + "/" + originalId.getPath();
//        Identifier id = EnderIO.rl(path);
//        return Optional.of(new RecipeHolder<>(id, recipe));
//    }
}
