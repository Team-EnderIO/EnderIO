package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.api.capability.IDarkSteelUpgradable;
import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.capability.DarkSteelUpgradeable;
import com.enderio.armory.common.init.ArmoryRecipes;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

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
        return DarkSteelUpgradeRegistry.instance().readUpgradeFromStack(pItemStack).isPresent();
    }

    @Override
    public boolean isBaseIngredient(ItemStack pItemStack) {
        return pItemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM) != null;
    }

    @Override
    public boolean isAdditionIngredient(ItemStack pItemStack) {
        return pItemStack.is(EIOItems.CONDUIT_BINDER.get());
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        // Check temporary binder
        if (!pContainer.getItem(2).is(EIOItems.CONDUIT_BINDER.get())) {
            return false;
        }

        // Check the upgrade can be applied to this item.
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pContainer.getItem(0));
        IDarkSteelUpgradable target = getUpgradableFromItem(pContainer.getItem(1));
        if (target != null) {
            return upgrade.map(target::canApplyUpgrade).orElse(false);
        }
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer, HolderLookup.Provider lookupProvider) {
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pContainer.getItem(0));

        ItemStack resultItem = pContainer.getItem(1).copy();
        IDarkSteelUpgradable target = getUpgradableFromItem(resultItem);
        if (target != null) {
            return upgrade.map(up -> DarkSteelUpgradeable.addUpgrade(resultItem, up)).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    private IDarkSteelUpgradable getUpgradableFromItem(ItemStack item) {
        return item.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
    }

    private Optional<IDarkSteelUpgrade> getUpgradeFromItem(ItemStack item) {
        return DarkSteelUpgradeRegistry.instance().readUpgradeFromStack(item);
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
        public static final StreamCodec<RegistryFriendlyByteBuf, DarkSteelUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
            (p_320158_, p_320396_) -> {},
            p_320376_ -> new DarkSteelUpgradeRecipe());

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
