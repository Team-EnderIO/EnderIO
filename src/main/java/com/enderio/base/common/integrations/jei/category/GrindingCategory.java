package com.enderio.base.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.base.common.integrations.jei.helper.FakeGrindingRecipe;
import com.enderio.base.common.lang.EIOLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GrindingCategory implements IRecipeCategory<FakeGrindingRecipe> {
    public static final RecipeType<FakeGrindingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "grinding", FakeGrindingRecipe.class);

    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/grindstone.png");

    private final IDrawable background;
    private final IDrawable icon;

    public GrindingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG_LOCATION, 28, 13, 120, 60);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.GRINDSTONE));
    }

    @Override
    public RecipeType<FakeGrindingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return EIOLang.JEI_GRINDING_CRAFTING_TITLE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, FakeGrindingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 6)
            .addItemStacks(recipe.topInput.getItems());
        if (recipe.bottomInput != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 21, 27).addItemStacks(recipe.bottomInput.getItems());
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 21)
            .addItemStack(recipe.result);
    }
}
