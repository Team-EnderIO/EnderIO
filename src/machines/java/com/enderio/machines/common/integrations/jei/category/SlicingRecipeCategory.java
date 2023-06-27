package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.machines.client.gui.screen.SlicerScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.integrations.jei.util.RecipeUtil;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.SlicingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.*;

public class SlicingRecipeCategory extends MachineRecipeCategory<SlicingRecipe> {

    public static final RecipeType<SlicingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "slicing", SlicingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SlicingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(SlicerScreen.BG_TEXTURE, 43, 15, 108, 60);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.SLICE_AND_SPLICE.get()));
    }

    @Override
    public RecipeType<SlicingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_SLICING;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlicingRecipe recipe, IFocusGroup focuses) {
        // Tool slots
        builder.addSlot(CATALYST, 11, 1)
            .addIngredients(Ingredient.of(ItemTags.AXES));
        builder.addSlot(CATALYST, 29, 1)
            .addIngredients(Ingredient.of(Tags.Items.SHEARS));

        builder.addSlot(INPUT, 1, 25)
            .addIngredients(recipe.getInputs().get(0));
        builder.addSlot(INPUT, 19, 25)
            .addIngredients(recipe.getInputs().get(1));
        builder.addSlot(INPUT, 37, 25)
            .addIngredients(recipe.getInputs().get(2));
        builder.addSlot(INPUT, 1, 43)
            .addIngredients(recipe.getInputs().get(3));
        builder.addSlot(INPUT, 19, 43)
            .addIngredients(recipe.getInputs().get(4));
        builder.addSlot(INPUT, 37, 43)
            .addIngredients(recipe.getInputs().get(5));

        // Output
        builder.addSlot(OUTPUT, 91, 34)
            .addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));
    }
}
