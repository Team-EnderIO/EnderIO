package com.enderio.machines.compat.jei.categories;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class AlloySmeltingCategory implements IRecipeCategory<AlloySmeltingRecipe> {
    public static final RecipeType<AlloySmeltingRecipe> RECIPE_TYPE = RecipeType.create(EnderIO.MODID, "alloy_smelting", AlloySmeltingRecipe.class);
    public static final Component TITLE = MachineLang.JEI_ALLOY_SMELTING_TITLE;

    private final IDrawableStatic background;
    private final IDrawableAnimated flame;
    private final IDrawableAnimated energy;
    private final IDrawable icon;

    public AlloySmeltingCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(EnderIO.MODID, "textures/gui/jei/jei_machines.png");
        background = guiHelper.createDrawable(location, 0, 0, 150, 74);

        IDrawableStatic flameDrawable = guiHelper.createDrawable(location, 150, 0, 14, 14);
        flame = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

        ResourceLocation overlay = new ResourceLocation(EnderIO.MODID, "textures/gui/overlay.png");
        IDrawableStatic energyDrawable = guiHelper.createDrawable(overlay, 0, 128, 9, 42);
        energy = guiHelper.createAnimatedDrawable(energyDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);

        icon = guiHelper.createDrawableItemStack(MachineBlocks.ALLOY_SMELTER.asStack());
    }

    @Override
    public RecipeType<AlloySmeltingRecipe> getRecipeType() {
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
    public void draw(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        energy.draw(stack, 4, 8);
        flame.draw(stack, 43, 29);
        flame.draw(stack, 91, 29);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloySmeltingRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 52);
        output.addItemStack(recipe.getResultStacks().get(0).getItem());

        IRecipeSlotBuilder input1 = builder.addSlot(RecipeIngredientRole.INPUT, 42, 11);
        input1.addItemStacks(List.of(recipe.getInputs().get(0).getItems()));

        IRecipeSlotBuilder input2 = builder.addSlot(RecipeIngredientRole.INPUT, 67, 1);
        if (recipe.getInputs().size() >= 2) input2.addItemStacks(List.of(recipe.getInputs().get(1).getItems()));

        IRecipeSlotBuilder input3 = builder.addSlot(RecipeIngredientRole.INPUT, 91, 11);
        if (recipe.getInputs().size() >= 3) input3.addItemStacks(List.of(recipe.getInputs().get(2).getItems()));

        IRecipeSlotBuilder capacitor = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 55);
        capacitor.addIngredients(Ingredient.of(EIOItems.BASIC_CAPACITOR.get(), EIOItems.DOUBLE_LAYER_CAPACITOR.get(), EIOItems.OCTADIC_CAPACITOR.get()));
    }
}
