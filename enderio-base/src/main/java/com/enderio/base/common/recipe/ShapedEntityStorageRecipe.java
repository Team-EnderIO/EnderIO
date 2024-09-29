package com.enderio.base.common.recipe;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.common.recipes.WrappedShapedRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Based upon BackpackUpgradeRecipe from Sophisticated Backpacks. Thanks!
 */
public class ShapedEntityStorageRecipe extends WrappedShapedRecipe {

    public static final Set<ShapedRecipe> REGISTERED_RECIPES = new LinkedHashSet<>();

    public ShapedEntityStorageRecipe(ShapedRecipe recipe) {
        super(recipe);

        REGISTERED_RECIPES.add(recipe);
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider lookupProvider) {
        ItemStack result = getWrapped().assemble(container, lookupProvider);

        getItemStoringEntity(container).ifPresent(itemStack ->
            result.set(EIODataComponents.STORED_ENTITY, itemStack.get(EIODataComponents.STORED_ENTITY)));
        return result;
    }

    @Override
    public boolean matches(CraftingInput pInv, Level pLevel) {
        // Only let this match if there is an entity storage to pull from
        return getItemStoringEntity(pInv).isPresent() && super.matches(pInv, pLevel);
    }

    private Optional<ItemStack> getItemStoringEntity(CraftingInput container) {
        for (int slot = 0; slot < container.size(); slot++) {
            ItemStack stack = container.getItem(slot);
            var data = stack.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);
            if (data.hasEntity()) {
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EIORecipes.SHAPED_ENTITY_STORAGE.get();
    }
}
