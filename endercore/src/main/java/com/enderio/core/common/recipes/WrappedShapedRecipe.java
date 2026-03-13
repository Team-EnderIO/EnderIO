package com.enderio.core.common.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

// Based on https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/recipe/WrappedShapedRecipe.java.
// Thanks to Mekanism
public abstract class WrappedShapedRecipe extends ShapedRecipe implements CraftingRecipe {

    private final ShapedRecipe wrapped;

    protected static <T extends WrappedShapedRecipe> MapCodec<T> createMapCodec(Function<ShapedRecipe, T> wrapper) {
        return ShapedRecipe.SERIALIZER.codec().xmap(wrapper, WrappedShapedRecipe::getWrapped);
    }

    protected static <T extends WrappedShapedRecipe> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(Function<ShapedRecipe, T> wrapper) {
        return  ShapedRecipe.SERIALIZER.streamCodec()
            .map(wrapper, WrappedShapedRecipe::getWrapped);
    }

    protected WrappedShapedRecipe(ShapedRecipe wrapped) {
        // Note the item stack template here should go unused.
        super(new CommonInfo(wrapped.showNotification()), new CraftingRecipe.CraftingBookInfo(wrapped.category(), wrapped.group()), wrapped.pattern,
            new ItemStackTemplate(Items.BARRIER));
        this.wrapped = wrapped;
    }

    public ShapedRecipe getWrapped() {
        return wrapped;
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        return wrapped.assemble(inv);
    }

    @Override
    public boolean matches(CraftingInput inv, Level world) {
        // Note: We do not override the matches method if it matches ignoring NBT,
        // to ensure that we return the proper value for if there is a match that gives
        // a proper output
        return wrapped.matches(inv, world) && !assemble(inv).isEmpty();
    }

    @Override
    public List<RecipeDisplay> display() {
        return wrapped.display();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        return wrapped.getRemainingItems(inv);
    }

    @Override
    public List<Optional<Ingredient>> getIngredients() {
        return wrapped.getIngredients();
    }

    @Override
    public boolean isSpecial() {
        return wrapped.isSpecial();
    }

    @Override
    public int getWidth() {
        return wrapped.getWidth();
    }

    @Override
    public int getHeight() {
        return wrapped.getHeight();
    }
}
