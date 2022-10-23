package com.enderio.machines.integration.jei.categories;

import com.enderio.EnderIO;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EnchanterCategory implements IRecipeCategory<EnchanterRecipe> {
    public static final RecipeType<EnchanterRecipe> RECIPE_TYPE = RecipeType.create(EnderIO.MODID, "enchanter", EnchanterRecipe.class);
    public static final Component TITLE = MachineLang.JEI_ENCHANTING_TITLE;

    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final IDrawable icon;

    public EnchanterCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(EnderIO.MODID, "textures/gui/jei/jei_machines.png");
        background = guiHelper.createDrawable(location, 0, 74, 146, 18);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 146, 74, 22, 15);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        icon = guiHelper.createDrawableItemStack(MachineBlocks.ENCHANTER.asStack());
    }

    @Override
    public RecipeType<EnchanterRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
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
    public void draw(EnchanterRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        arrow.draw(stack, 97, 2);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchanterRecipe recipe, IFocusGroup focuses) {
        int level = 1;

        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 129, 1);
        output.addItemStack(recipe.getBookForLevel(level));

        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
        input.addIngredients(recipe.getInput());

        IRecipeSlotBuilder book = builder.addSlot(RecipeIngredientRole.INPUT, 50, 1);
        book.addItemStack(new ItemStack(Items.BOOK));

        IRecipeSlotBuilder lapis = builder.addSlot(RecipeIngredientRole.INPUT, 70, 1);
        lapis.addItemStack(new ItemStack(Items.LAPIS_LAZULI, recipe.getLapisForLevel(level)));
    }
}
