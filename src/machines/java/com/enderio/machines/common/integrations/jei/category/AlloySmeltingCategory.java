package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY;

public class AlloySmeltingCategory extends MachineRecipeCategory<AlloySmeltingRecipe> {

    public static final RecipeType<AlloySmeltingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "alloy_smelting", AlloySmeltingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic staticFlame;
    private final IDrawable animatedFlame;

    public AlloySmeltingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(AlloySmelterScreen.BG_TEXTURE_AUTO, 53, 6, 67 + 40, 73); // + 40 text space
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.ALLOY_SMELTER.get()));

        staticFlame = guiHelper.createDrawable(StirlingGeneratorScreen.BG_TEXTURE, 176, 0, 14, 14);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.BOTTOM, false);
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

        if (!inputs.isEmpty()) {
            builder.addSlot(INPUT, 1, 11)
                .addItemStacks(inputs.get(0).getItems());
        } else {
            builder.addSlot(RENDER_ONLY, 1, 11);
        }

        if (inputs.size() > 1) {
            builder.addSlot(INPUT, 26, 1)
               .addItemStacks(inputs.get(1).getItems());
        } else {
            builder.addSlot(RENDER_ONLY, 26, 11);
        }

        if (inputs.size() > 2) {
            builder.addSlot(INPUT, 50, 11)
                .addItemStacks(inputs.get(2).getItems());
        } else {
            builder.addSlot(RENDER_ONLY, 50, 11);
        }

        builder.addSlot(OUTPUT, 26, 52)
            .addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));
    }

    @Override
    public void draw(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        animatedFlame.draw(guiGraphics, 3, 29);
        animatedFlame.draw(guiGraphics, 51, 29);
        guiGraphics.drawString(Minecraft.getInstance().font, getBasicEnergyString(recipe), 60, 50, 0xff808080, false);
    }

    @Override
    public List<Component> getTooltipStrings(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 60 && mouseY > 50 && mouseX < 60 + mc.font.width(getBasicEnergyString(recipe)) && mouseY < 50 + mc.font.lineHeight) {
            return List.of(MachineLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }

        return List.of();
    }
}
