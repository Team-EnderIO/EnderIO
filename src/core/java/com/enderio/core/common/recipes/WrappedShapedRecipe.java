package com.enderio.core.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// Based on https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/recipe/WrappedShapedRecipe.java.
// Thanks to Mekanism
public abstract class WrappedShapedRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {
    private final ShapedRecipe wrapped;

    protected WrappedShapedRecipe(ShapedRecipe wrapped) {
        this.wrapped = wrapped;
    }

    public ShapedRecipe getWrapped() {
        return wrapped;
    }

    @Override
    public CraftingBookCategory category() {
        return wrapped.category();
    }

    @Override
    public abstract ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess);

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        //Note: We do not override the matches method if it matches ignoring NBT,
        // to ensure that we return the proper value for if there is a match that gives a proper output
        return wrapped.matches(inv, world) && !assemble(inv, world.registryAccess()).isEmpty();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return wrapped.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return wrapped.getResultItem(registryAccess);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        return wrapped.getRemainingItems(inv);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return wrapped.getIngredients();
    }

    @Override
    public boolean isSpecial() {
        return wrapped.isSpecial();
    }

    @Override
    public String getGroup() {
        return wrapped.getGroup();
    }

    @Override
    public ItemStack getToastSymbol() {
        return wrapped.getToastSymbol();
    }

    @Override
    public int getRecipeWidth() {
        return wrapped.getRecipeWidth();
    }

    @Override
    public int getRecipeHeight() {
        return wrapped.getRecipeHeight();
    }

    @Override
    public boolean isIncomplete() {
        return wrapped.isIncomplete();
    }

    public static class Serializer<T extends WrappedShapedRecipe> implements RecipeSerializer<T> {
        private final Function<ShapedRecipe, T> wrapper;
        private Codec<T> codec;

        public Serializer(Function<ShapedRecipe, T> wrapper) {
            this.wrapper = wrapper;
        }

        @NotNull
        @Override
        public Codec<T> codec() {
            if (codec == null) {
                codec = ((MapCodec.MapCodecCodec<ShapedRecipe>) RecipeSerializer.SHAPED_RECIPE.codec()).codec()
                    .xmap(wrapper, WrappedShapedRecipe::getWrapped).codec();
            }
            return codec;
        }

        @NotNull
        @Override
        public T fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return wrapper.apply(RecipeSerializer.SHAPED_RECIPE.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull T recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.getWrapped());
        }
    }
}
