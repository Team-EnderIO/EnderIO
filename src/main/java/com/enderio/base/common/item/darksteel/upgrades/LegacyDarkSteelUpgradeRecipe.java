package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.api.capability.IDarkSteelUpgradable;
import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.base.common.capability.DarkSteelUpgradeable;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIORecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Optional;

@Deprecated(forRemoval = true, since = "1.19.4")
public class LegacyDarkSteelUpgradeRecipe extends LegacyUpgradeRecipe {

    public LegacyDarkSteelUpgradeRecipe(ResourceLocation pRecipeId) {
        super(pRecipeId, Ingredient.EMPTY,Ingredient.EMPTY,ItemStack.EMPTY);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(Container pInv, Level pLevel) {
        Optional<IDarkSteelUpgradable> target = getUpgradableFromItem(pInv.getItem(0));
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pInv.getItem(1));
        return target.map(upgradable -> upgrade.map(upgradable::canApplyUpgrade).orElse(false)).orElse(false);
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        ItemStack resultItem = pContainer.getItem(0).copy();
        Optional<IDarkSteelUpgradable> target = getUpgradableFromItem(resultItem);
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pContainer.getItem(1));
        return target.map(upgradable -> upgrade.map(up -> DarkSteelUpgradeable.addUpgrade(resultItem, up)).orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
    }

    private Optional<IDarkSteelUpgradable> getUpgradableFromItem(ItemStack item) {
        return item.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).resolve();
    }

    private Optional<IDarkSteelUpgrade> getUpgradeFromItem(ItemStack item) {
        return DarkSteelUpgradeRegistry.instance().readUpgradeFromStack(item);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EIORecipes.DARK_STEEL_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<LegacyDarkSteelUpgradeRecipe> {

        @Override
        public LegacyDarkSteelUpgradeRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            return new LegacyDarkSteelUpgradeRecipe(pRecipeId);
        }

        @Override
        public LegacyDarkSteelUpgradeRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            return new LegacyDarkSteelUpgradeRecipe(pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, LegacyDarkSteelUpgradeRecipe pRecipe) {
        }

    }
}

