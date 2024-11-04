package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.machines.client.gui.screen.PrimitiveAlloySmelterScreen;
import com.enderio.machines.client.gui.screen.StirlingGeneratorScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.integrations.jei.util.RecipeUtil;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.*;

public class PrimitiveAlloySmeltingCategory extends MachineRecipeCategory<AlloySmeltingRecipe> {

    public static final RecipeType<AlloySmeltingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "primitive_alloy_smelting", AlloySmeltingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic staticFlame;
    private final IDrawable animatedFlame;

    public PrimitiveAlloySmeltingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(PrimitiveAlloySmelterScreen.BG_TEXTURE, 19, 16, 118, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get()));

        staticFlame = guiHelper.createDrawable(StirlingGeneratorScreen.BG_TEXTURE, 176, 0, 14, 14);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);
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

        if (!inputs.isEmpty()) {
            builder.addSlot(INPUT, 1, 1)
                .addItemStacks(inputs.get(0).getItems());
        } else {
            builder.addSlot(RENDER_ONLY, 1, 1);
        }

        if (inputs.size() > 1) {
            builder.addSlot(INPUT, 21, 1)
                .addItemStacks(inputs.get(1).getItems());
        } else {
            builder.addSlot(RENDER_ONLY, 21, 1);
        }

        if (inputs.size() > 2) {
            builder.addSlot(INPUT, 41, 1)
                .addItemStacks(inputs.get(2).getItems());
        } else {
            builder.addSlot(RENDER_ONLY, 41, 1);
        }

        builder.addSlot(OUTPUT, 97, 19)
            .addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));
    }

    @Override
    public void draw(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        animatedFlame.draw(guiGraphics, 22, 20);

        // TODO: Draw time to smelt
    }
}
