package com.enderio.base.common.recipe;

import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.common.recipes.WrappedShapedRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
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
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = getWrapped().assemble(container, registryAccess);

        getItemStoringEntity(container).ifPresent(itemStack ->
            result.setData(EIOAttachments.STORED_ENTITY, itemStack.getData(EIOAttachments.STORED_ENTITY)));
        return result;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        // Only let this match if there is an entity storage to pull from
        return getItemStoringEntity(pInv).isPresent() && super.matches(pInv, pLevel);
    }

    private Optional<ItemStack> getItemStoringEntity(CraftingContainer container) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);

            if (stack.getCapability(EIOCapabilities.StoredEntity.ITEM) != null) {
                var data = stack.getData(EIOAttachments.STORED_ENTITY);
                if (data.hasEntity()) {
                    return Optional.of(stack);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EIORecipes.SHAPED_ENTITY_STORAGE.get();
    }
}
