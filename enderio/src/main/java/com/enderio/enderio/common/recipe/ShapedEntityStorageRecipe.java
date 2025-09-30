package com.enderio.enderio.common.recipe;

import com.enderio.core.common.recipes.WrappedShapedRecipe;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.common.init.EIORecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

/**
 * Based upon BackpackUpgradeRecipe from Sophisticated Backpacks. Thanks!
 */
public class ShapedEntityStorageRecipe extends WrappedShapedRecipe {
    
    public ShapedEntityStorageRecipe(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider lookupProvider) {
        ItemStack result = getWrapped().assemble(container, lookupProvider);

        getItemStoringEntity(container).ifPresent(itemStack -> {
            var inputSoulStorage = Objects.requireNonNull(itemStack.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM));
            SoulBoundUtils.tryBindSoul(result, inputSoulStorage.getBoundSoul());
        });
        
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
            var soulStorage = stack.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
            if (soulStorage != null && soulStorage.hasSoul()) {
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
