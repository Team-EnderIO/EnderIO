package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.machines.client.gui.screen.PrimitiveAlloySmelterScreen;
import com.enderio.machines.common.integrations.jei.util.MachineCategory;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.RecipeUtil;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class PrimitiveAlloySmeltingCategory extends MachineCategory<AlloySmeltingRecipe> {

    public static final RecipeType<AlloySmeltingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "primitive_alloy_smelting", AlloySmeltingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public PrimitiveAlloySmeltingCategory(IGuiHelper guiHelper) {
        super(guiHelper, true);
        this.background = guiHelper.createDrawable(PrimitiveAlloySmelterScreen.BG_TEXTURE, 19, 16, 118, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get()));
    }

    @Override
    public RecipeType<AlloySmeltingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_PRIMITIVE_ALLOY_SMELTING;
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
    public void setRecipe(IRecipeLayoutBuilder builder, AlloySmeltingRecipe recipe, IFocusGroup focuses) {
        List<CountedIngredient> inputs = recipe.getInputs();

        if (inputs.size() > 0)
            builder.addSlot(INPUT, 1, 1)
                .addItemStacks(inputs.get(0).getItems());

        if (inputs.size() > 1)
            builder.addSlot(INPUT, 21, 1)
                .addItemStacks(inputs.get(1).getItems());

        if (inputs.size() > 2)
            builder.addSlot(INPUT, 41, 1)
                .addItemStacks(inputs.get(2).getItems());

        builder.addSlot(OUTPUT, 97, 19)
            .addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));
    }

    @Override
    public void draw(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        animatedFlame.draw(stack, 22, 20);

        // TODO: Draw time to smelt
    }
}
