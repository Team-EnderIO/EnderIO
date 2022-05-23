package com.enderio.machines.common.integration.jei.categories;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.TooltipUtil;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integration.jei.JEIPlugin;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.List;

public class AlloySmeltingCategory implements IRecipeCategory<AlloySmeltingRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public AlloySmeltingCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(AlloySmelterScreen.BG_TEXTURE_AUTO, 53, 6, 67 + 40, 73);// + 40 for text space
        icon = guiHelper.createDrawableIngredient(new ItemStack(MachineBlocks.ALLOY_SMELTER.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return JEIPlugin.CATEGORY_ALLOY_SMELTING;
    }

    @Override
    public Class<? extends AlloySmeltingRecipe> getRecipeClass() {
        return AlloySmeltingRecipe.class;
    }

    @Override
    public Component getTitle() {
        return MachineBlocks.ALLOY_SMELTER.get().getName();
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
    public void setIngredients(AlloySmeltingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM_STACK, recipe.getAllInputs());
        ingredients.setOutputs(VanillaTypes.ITEM_STACK, recipe.getAllOutputs());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AlloySmeltingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 0, 10);
        guiItemStacks.init(1, true, 25, 0);
        guiItemStacks.init(2, true, 49, 10);
        guiItemStacks.init(3, false, 25, 51);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(AlloySmeltingRecipe recipe, PoseStack stack, double mouseX, double mouseY) {
        Minecraft.getInstance().font.draw(stack, getEnergyString(recipe), 60, 50, 0xff808080);
    }

    @Override
    public List<Component> getTooltipStrings(AlloySmeltingRecipe recipe, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 60 && mouseY > 50 && mouseX < 60 + mc.font.width(getEnergyString(recipe)) && mouseY < 50 + mc.font.lineHeight) {
            return List.of(MachineLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }

        return List.of();
    }

    private TranslatableComponent getEnergyString(AlloySmeltingRecipe recipe) {
        return TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, NumberFormat.getIntegerInstance().format(recipe.getEnergyCost()));
    }
}