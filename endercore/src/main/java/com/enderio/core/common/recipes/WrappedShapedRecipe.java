package com.enderio.core.common.recipes;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

// Based on https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/recipe/WrappedShapedRecipe.java.
// Thanks to Mekanism
public abstract class WrappedShapedRecipe extends ShapedRecipe implements CraftingRecipe {
    private final ShapedRecipe wrapped;

    protected WrappedShapedRecipe(ShapedRecipe wrapped) {
        super(wrapped.getGroup(), wrapped.category(), wrapped.pattern, ItemStack.EMPTY, wrapped.showNotification());
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
    public abstract ItemStack assemble(CraftingInput inv, HolderLookup.Provider lookupProvider);

    @Override
    public boolean matches(CraftingInput inv, Level world) {
        // Note: We do not override the matches method if it matches ignoring NBT,
        // to ensure that we return the proper value for if there is a match that gives
        // a proper output
        return wrapped.matches(inv, world) && !assemble(inv, world.registryAccess()).isEmpty();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return wrapped.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return wrapped.getResultItem(lookupProvider);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
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
    public int getWidth() {
        return wrapped.getWidth();
    }

    @Override
    public int getHeight() {
        return wrapped.getHeight();
    }

    @Override
    public boolean isIncomplete() {
        return wrapped.isIncomplete();
    }

    public static class Serializer<T extends WrappedShapedRecipe> implements RecipeSerializer<T> {
        private final Function<ShapedRecipe, T> wrapper;
        private MapCodec<T> codec;
        private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(Function<ShapedRecipe, T> wrapper) {
            this.wrapper = wrapper;
        }

        @NotNull
        @Override
        public MapCodec<T> codec() {
            if (codec == null) {
                codec = RecipeSerializer.SHAPED_RECIPE.codec().xmap(wrapper, WrappedShapedRecipe::getWrapped);
            }

            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            if (streamCodec == null) {
                streamCodec = RecipeSerializer.SHAPED_RECIPE.streamCodec()
                        .map(wrapper, WrappedShapedRecipe::getWrapped);
            }

            return streamCodec;
        }
    }
}
