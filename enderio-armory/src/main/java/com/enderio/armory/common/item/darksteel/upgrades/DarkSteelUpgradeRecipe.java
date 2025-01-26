package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.armory.common.init.ArmoryCapabilities;
import com.enderio.armory.common.init.ArmoryRecipes;
import com.enderio.armory.common.item.darksteel.DarkSteelUpgradeItem;
import com.enderio.base.common.init.EIOItems;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

// TODO: Change this into a anvil recipe.
public class DarkSteelUpgradeRecipe extends SmithingTransformRecipe {
    public DarkSteelUpgradeRecipe() {
        super(Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack pItemStack) {
        return DarkSteelUpgradeItem.readUpgradeFromStack(pItemStack).isPresent();
    }

    @Override
    public boolean isBaseIngredient(ItemStack pItemStack) {
        return pItemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY) != null;
    }

    @Override
    public boolean isAdditionIngredient(ItemStack pItemStack) {
        return pItemStack.is(EIOItems.CONDUIT_BINDER.get());
    }

    @Override
    public boolean matches(SmithingRecipeInput recipeInput, Level pLevel) {
        // Check temporary binder
        if (!recipeInput.getItem(2).is(EIOItems.CONDUIT_BINDER.get())) {
            return false;
        }

        // Check the upgrade can be applied to this item.
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(recipeInput.getItem(0));
        @Nullable
        DarkSteelCapability target = getUpgradableFromItem(recipeInput.getItem(1));
        if (target != null) {
            return upgrade.map(target::canApplyUpgrade).orElse(false);
        }
        return false;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput recipeInput, HolderLookup.Provider lookupProvider) {
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(recipeInput.getItem(0));

        ItemStack resultItem = recipeInput.getItem(1).copy();
        @Nullable
        DarkSteelCapability target = getUpgradableFromItem(resultItem);
        if (target != null) {
            return upgrade.map(up -> DarkSteelCapability.addUpgrade(resultItem, up)).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    private DarkSteelCapability getUpgradableFromItem(ItemStack item) {
        return item.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
    }

    private Optional<IDarkSteelUpgrade> getUpgradeFromItem(ItemStack item) {
        return DarkSteelUpgradeItem.readUpgradeFromStack(item);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ArmoryRecipes.DARK_STEEL_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<DarkSteelUpgradeRecipe> {
        public static final MapCodec<DarkSteelUpgradeRecipe> CODEC = MapCodec.unit(new DarkSteelUpgradeRecipe());
        public static final StreamCodec<RegistryFriendlyByteBuf, DarkSteelUpgradeRecipe> STREAM_CODEC = StreamCodec
                .of((p_320158_, p_320396_) -> {
                }, p_320376_ -> new DarkSteelUpgradeRecipe());

        @Override
        public MapCodec<DarkSteelUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DarkSteelUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
