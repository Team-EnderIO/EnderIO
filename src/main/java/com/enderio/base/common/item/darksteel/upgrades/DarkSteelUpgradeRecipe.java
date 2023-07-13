package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.api.capability.IDarkSteelUpgradable;
import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.base.common.capability.DarkSteelUpgradeable;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIORecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// TODO: Change this into a anvil recipe.
public class DarkSteelUpgradeRecipe extends SmithingTransformRecipe {
    public DarkSteelUpgradeRecipe(ResourceLocation pId) {
        super(pId, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack pItemStack) {
        return DarkSteelUpgradeRegistry.instance().readUpgradeFromStack(pItemStack).isPresent();
    }

    @Override
    public boolean isBaseIngredient(ItemStack pItemStack) {
        return pItemStack.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).resolve().isPresent();
    }

    @Override
    public boolean isAdditionIngredient(ItemStack pItemStack) {
        return pItemStack.is(EIOItems.CONDUIT_BINDER.get());
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        // Check temporary binder
        if (!pContainer.getItem(2).is(EIOItems.CONDUIT_BINDER.get()))
            return false;

        // Check the upgrade can be applied to this item.
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pContainer.getItem(0));
        Optional<IDarkSteelUpgradable> target = getUpgradableFromItem(pContainer.getItem(1));
        return target.map(upgradable -> upgrade.map(upgradable::canApplyUpgrade).orElse(false)).orElse(false);
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pContainer.getItem(0));

        ItemStack resultItem = pContainer.getItem(1).copy();
        Optional<IDarkSteelUpgradable> target = getUpgradableFromItem(resultItem);

        return target.map(upgradable -> upgrade.map(up -> DarkSteelUpgradeable.addUpgrade(resultItem, up)).orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
    }

    private Optional<IDarkSteelUpgradable> getUpgradableFromItem(ItemStack item) {
        return item.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).resolve();
    }

    private Optional<IDarkSteelUpgrade> getUpgradeFromItem(ItemStack item) {
        return DarkSteelUpgradeRegistry.instance().readUpgradeFromStack(item);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267209_) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EIORecipes.DARK_STEEL_UPGRADE_1_20.get();
    }

    public static class Serializer implements RecipeSerializer<DarkSteelUpgradeRecipe> {
        @Override
        public DarkSteelUpgradeRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            return new DarkSteelUpgradeRecipe(pRecipeId);
        }

        @Override
        public @Nullable DarkSteelUpgradeRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            return new DarkSteelUpgradeRecipe(pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, DarkSteelUpgradeRecipe pRecipe) {
        }
    }
}
