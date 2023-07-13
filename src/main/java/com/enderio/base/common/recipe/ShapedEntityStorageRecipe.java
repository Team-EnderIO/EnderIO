package com.enderio.base.common.recipe;

import com.enderio.api.capability.IEntityStorage;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIORecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Based upon BackpackUpgradeRecipe from Sophisticated Backpacks. Thanks!
 */
public class ShapedEntityStorageRecipe extends ShapedRecipe {

    public static final Set<ResourceLocation> REGISTERED_RECIPES = new LinkedHashSet<>();

    public ShapedEntityStorageRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(),
            recipe.result); // Gross, but better than always passing null

        REGISTERED_RECIPES.add(recipe.getId());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(container, registryAccess);
        getInputEntityStorage(container).ifPresent(inputStorage -> {
            result.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(resultStorage -> {
                resultStorage.setStoredEntityData(inputStorage.getStoredEntityData());
            });
        });
        return result;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        // Only let this match if there is an entity storage to pull from
        return getInputEntityStorage(pInv).isPresent() && super.matches(pInv, pLevel);
    }

    private LazyOptional<IEntityStorage> getInputEntityStorage(CraftingContainer container) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);

            LazyOptional<IEntityStorage> storage = stack.getCapability(EIOCapabilities.ENTITY_STORAGE);
            if (storage.isPresent())
                return storage;
        }

        return LazyOptional.empty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EIORecipes.SHAPED_ENTITY_STORAGE.get();
    }

    public static class Serializer implements RecipeSerializer<ShapedEntityStorageRecipe> {

        @Override
        public ShapedEntityStorageRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            return new ShapedEntityStorageRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(pRecipeId, pSerializedRecipe));
        }

        @Override
        public @Nullable ShapedEntityStorageRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            var shaped = RecipeSerializer.SHAPED_RECIPE.fromNetwork(pRecipeId, pBuffer);
            if (shaped == null)
                return null;
            return new ShapedEntityStorageRecipe(shaped);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ShapedEntityStorageRecipe pRecipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(pBuffer, pRecipe);
        }
    }
}
