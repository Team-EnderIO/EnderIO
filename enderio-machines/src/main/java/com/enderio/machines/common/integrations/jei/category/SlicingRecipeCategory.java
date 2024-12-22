package com.enderio.machines.common.integrations.jei.category;

import static mezz.jei.api.recipe.RecipeIngredientRole.CATALYST;
import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

import com.enderio.EnderIOBase;
import com.enderio.base.common.integrations.jei.JEIUtils;
import com.enderio.machines.common.blocks.slicer.SlicingRecipe;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.integrations.jei.util.RecipeUtil;
import com.enderio.machines.common.lang.MachineLang;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.Tags;

public class SlicingRecipeCategory extends MachineRecipeCategory<RecipeHolder<SlicingRecipe>> {

    public static final RecipeType<RecipeHolder<SlicingRecipe>> TYPE = JEIUtils
            .createRecipeType(EnderIOBase.REGISTRY_NAMESPACE, "slicing", SlicingRecipe.class);

    public static final ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/viewer/slice_and_splice.png");
    private static final int WIDTH = 108;
    private static final int HEIGHT = 60;

    private final IDrawable background;
    private final IDrawable icon;

    public SlicingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.SLICE_AND_SPLICE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<SlicingRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SlicingRecipe> recipe, IFocusGroup focuses) {
        // Tool slots
        builder.addSlot(CATALYST, 11, 1).addIngredients(Ingredient.of(ItemTags.AXES));
        builder.addSlot(CATALYST, 29, 1).addIngredients(Ingredient.of(Tags.Items.TOOLS_SHEAR));

        builder.addSlot(INPUT, 1, 25).addIngredients(recipe.value().inputs().get(0));
        builder.addSlot(INPUT, 19, 25).addIngredients(recipe.value().inputs().get(1));
        builder.addSlot(INPUT, 37, 25).addIngredients(recipe.value().inputs().get(2));
        builder.addSlot(INPUT, 1, 43).addIngredients(recipe.value().inputs().get(3));
        builder.addSlot(INPUT, 19, 43).addIngredients(recipe.value().inputs().get(4));
        builder.addSlot(INPUT, 37, 43).addIngredients(recipe.value().inputs().get(5));

        // Output
        builder.addSlot(OUTPUT, 91, 34).addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));
    }
}
