package com.enderio.machines.compat.jei.categories;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.SagMillingRecipe;
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
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;

public class SagMillCategory implements IRecipeCategory<SagMillingRecipe> {
    public static final RecipeType<SagMillingRecipe> RECIPE_TYPE = RecipeType.create(EnderIO.MODID, "sag_mill", SagMillingRecipe.class);
    public static final Component TITLE = MachineLang.JEI_SAG_MILLING_TITLE;

    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated gindball;
    private final IDrawableAnimated energy;
    private final IDrawable icon;

    public SagMillCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(EnderIO.MODID, "textures/gui/jei/gui_machines.png");
        background = guiHelper.createDrawable(location, 0, 92, 153, 69);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 150, 14, 15, 23);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);

        IDrawableStatic gindballDrawable = guiHelper.createDrawable(location, 150, 37, 4, 16);
        gindball = guiHelper.createAnimatedDrawable(gindballDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);

        ResourceLocation overlay = new ResourceLocation(EnderIO.MODID, "textures/gui/overlay.png");
        IDrawableStatic energyDrawable = guiHelper.createDrawable(overlay, 0, 128, 9, 42);
        energy = guiHelper.createAnimatedDrawable(energyDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);

        ResourceLocation iconLocation = new ResourceLocation(EnderIO.MODID, "textures/gui/jei/icons/alloy_smelter.png");
        icon = guiHelper.createDrawableItemStack(MachineBlocks.ALLOY_SMELTER.asStack());
    }

    @Override
    public RecipeType<SagMillingRecipe> getRecipeType() {
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
    public void draw(SagMillingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        arrow.draw(stack, 69, 21);
        gindball.draw(stack, 130, 12);
        energy.draw(stack, 4, 3);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SagMillingRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 68, 1);
        input.addIngredients(recipe.getInput());

        IRecipeSlotBuilder output1 = builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 48);
        output1.addItemStack(recipe.getResultStacks().get(0).getItem()); // TODO: Setup outputs, right now it is a placeholder

        IRecipeSlotBuilder output2 = builder.addSlot(RecipeIngredientRole.OUTPUT, 58, 48);
        output1.addItemStack(recipe.getResultStacks().get(0).getItem());

        IRecipeSlotBuilder output3 = builder.addSlot(RecipeIngredientRole.OUTPUT, 79, 48);
        output1.addItemStack(recipe.getResultStacks().get(0).getItem());

        IRecipeSlotBuilder output4 = builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 48);
        output1.addItemStack(recipe.getResultStacks().get(0).getItem());

        // Render Only
        IRecipeSlotBuilder capacitor = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 49);
        capacitor.addIngredients(Ingredient.of(EIOItems.BASIC_CAPACITOR.get(), EIOItems.DOUBLE_LAYER_CAPACITOR.get(), EIOItems.OCTADIC_CAPACITOR.get()));

        IRecipeSlotBuilder grindball = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 110, 12);
        grindball.addIngredients(Ingredient.of(EIOItems.SILICON.get())); // TODO: Replace with grindballs
    }
}
