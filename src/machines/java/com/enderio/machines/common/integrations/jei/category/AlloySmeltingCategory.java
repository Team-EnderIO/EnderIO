package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class AlloySmeltingCategory extends MachineCategory<AlloySmeltingRecipe> {

    public static final RecipeType<AlloySmeltingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "alloy_smelting", AlloySmeltingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public AlloySmeltingCategory(IGuiHelper guiHelper) {
        super(guiHelper, false);
        this.background = guiHelper.createDrawable(AlloySmelterScreen.BG_TEXTURE_AUTO, 53, 6, 67 + 40, 73); // + 40 text space
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.ALLOY_SMELTER.get()));
    }

    @Override
    public RecipeType<AlloySmeltingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_ALLOY_SMELTING;
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
            builder.addSlot(INPUT, 1, 11)
                .addItemStacks(inputs.get(0).getItems());

        if (inputs.size() > 1)
            builder.addSlot(INPUT, 26, 1)
               .addItemStacks(inputs.get(1).getItems());

        if (inputs.size() > 2)
            builder.addSlot(INPUT, 50, 11)
                .addItemStacks(inputs.get(2).getItems());

        builder.addSlot(OUTPUT, 26, 52)
            .addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));
    }

    @Override
    public void draw(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        animatedFlame.draw(stack, 3, 29);
        animatedFlame.draw(stack, 51, 29);
        Minecraft.getInstance().font.draw(stack, getEnergyString(recipe), 60, 50, 0xff808080);
    }

    @Override
    public List<Component> getTooltipStrings(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 60 && mouseY > 50 && mouseX < 60 + mc.font.width(getEnergyString(recipe)) && mouseY < 50 + mc.font.lineHeight) {
            return List.of(MachineLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }

        return List.of();
    }

    private Component getEnergyString(AlloySmeltingRecipe recipe) {
        return TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, NumberFormat.getIntegerInstance(Locale.ENGLISH).format(recipe.getBasicEnergyCost()));
    }
}
