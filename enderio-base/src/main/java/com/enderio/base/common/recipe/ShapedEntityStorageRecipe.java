package com.enderio.base.common.recipe;

import com.enderio.base.api.soul.SoulBoundUtils;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.common.recipes.WrappedShapedRecipe;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

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
            var inputSoulStorage = Objects.requireNonNull(itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM));
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
            var soulStorage = stack.getCapability(EIOCapabilities.SoulBindable.ITEM);
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
