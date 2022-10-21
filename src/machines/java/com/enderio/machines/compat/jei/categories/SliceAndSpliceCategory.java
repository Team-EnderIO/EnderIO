package com.enderio.machines.compat.jei.categories;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.SlicingRecipe;
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
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

public class SliceAndSpliceCategory implements IRecipeCategory<SlicingRecipe> {
    public static final RecipeType<SlicingRecipe> RECIPE_TYPE = RecipeType.create(EnderIO.MODID, "slicing", SlicingRecipe.class);
    public static final Component TITLE = MachineLang.JEI_SLICING_TITLE;

    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated energy;
    private final IDrawable icon;

    public SliceAndSpliceCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(EnderIO.MODID, "textures/gui/jei/jei_machines.png");
        background = guiHelper.createDrawable(location, 0, 161, 146, 67);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 146, 74, 22, 15);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        ResourceLocation overlay = new ResourceLocation(EnderIO.MODID, "textures/gui/overlay.png");
        IDrawableStatic energyDrawable = guiHelper.createDrawable(overlay, 0, 128, 9, 42);
        energy = guiHelper.createAnimatedDrawable(energyDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);

        icon = guiHelper.createDrawableItemStack(MachineBlocks.SLICE_AND_SPLICE.asStack());
    }

    @Override
    public RecipeType<SlicingRecipe> getRecipeType() {
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
    public void draw(SlicingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        arrow.draw(stack, 92, 36);
        energy.draw(stack, 4, 1);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlicingRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 122, 36);
        output.addItemStack(recipe.getResultStacks().get(0).getItem());

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int count = ingredients.size();

        IRecipeSlotBuilder input1 = builder.addSlot(RecipeIngredientRole.INPUT, 32, 27);
        input1.addIngredients(ingredients.get(0));

        IRecipeSlotBuilder input2 = builder.addSlot(RecipeIngredientRole.INPUT, 50, 27);
        if (count >= 2) input2.addIngredients(ingredients.get(1));

        IRecipeSlotBuilder input3 = builder.addSlot(RecipeIngredientRole.INPUT, 68, 27);
        if (count >= 3) input3.addIngredients(ingredients.get(2));

        IRecipeSlotBuilder input4 = builder.addSlot(RecipeIngredientRole.INPUT, 32, 45);
        if (count >= 4) input4.addIngredients(ingredients.get(3));

        IRecipeSlotBuilder input5 = builder.addSlot(RecipeIngredientRole.INPUT, 50, 45);
        if (count >= 5) input5.addIngredients(ingredients.get(4));

        IRecipeSlotBuilder input6 = builder.addSlot(RecipeIngredientRole.INPUT, 68, 45);
        if (count >= 6) input6.addIngredients(ingredients.get(5));


        // Tools
        IRecipeSlotBuilder axe = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 42, 3);
        axe.addIngredients(Ingredient.of(Tags.Items.TOOLS_AXES));

        IRecipeSlotBuilder shears = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 60, 3);
        shears.addIngredients(Ingredient.of(Tags.Items.SHEARS));

        IRecipeSlotBuilder capacitor = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 47);
        capacitor.addIngredients(Ingredient.of(EIOItems.BASIC_CAPACITOR.get(), EIOItems.DOUBLE_LAYER_CAPACITOR.get(), EIOItems.OCTADIC_CAPACITOR.get())); // TODO: Use tags for capacitors
    }
}
