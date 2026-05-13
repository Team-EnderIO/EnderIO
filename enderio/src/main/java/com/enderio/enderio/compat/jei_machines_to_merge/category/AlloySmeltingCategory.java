package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.core.common.util.IngredientUtility;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.recipes.alloy.AlloySmeltingRecipeDisplay;
import com.enderio.enderio.client.content.machines.gui.screen.StirlingGeneratorScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.*;

public class AlloySmeltingCategory extends MachineRecipeCategory<RecipeHolder<AlloySmeltingRecipe>> {

    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/viewer/alloy_smelter.png");
    private static final int WIDTH = 67;
    private static final int HEIGHT = 73;

    public static final IRecipeType<RecipeHolder<AlloySmeltingRecipe>> TYPE = JEIUtils
            .createRecipeType(EnderIO.MOD_ID, "alloy_smelting", AlloySmeltingRecipe.class);

    private final IDrawable icon;
    private final IDrawableStatic background;
    private final IDrawableStatic staticFlame;
    private final IDrawable animatedFlame;

    public AlloySmeltingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.ALLOY_SMELTER.get()));
        this.background = guiHelper.createDrawable(BG_TEXTURE, 0, 0, WIDTH, HEIGHT);

        // TODO: Swap to our sprites.
        staticFlame = guiHelper.createDrawable(StirlingGeneratorScreen.BG_TEXTURE, 176, 0, 14, 14);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.BOTTOM,
                false);
    }

    @Override
    public IRecipeType<RecipeHolder<AlloySmeltingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.ALLOY_SMELTING_TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlloySmeltingRecipe> recipeHolder, IFocusGroup focuses) {
        AlloySmeltingRecipe recipe = recipeHolder.value();
        RecipeDisplay display = recipe.display().getFirst();
        if (display instanceof AlloySmeltingRecipeDisplay alloySmelterDisplay) {
            var ingredients = alloySmelterDisplay.ingredients();

            if (!ingredients.isEmpty()) {
                builder.addInputSlot(1, 11).add(ingredients.getFirst());
            } else {
                builder.addSlot(RENDER_ONLY, 1, 11);
            }

            if (ingredients.size() > 1) {
                builder.addInputSlot(26, 1).add(ingredients.get(1));
            } else {
                builder.addSlot(RENDER_ONLY, 26, 1);
            }

            if (ingredients.size() > 2) {
                builder.addInputSlot(50, 11).add(ingredients.get(2));
            } else {
                builder.addSlot(RENDER_ONLY, 50, 11);
            }

            builder.addOutputSlot(26, 52)
//                .setOutputSlotBackground()
                .add(alloySmelterDisplay.result());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<AlloySmeltingRecipe> recipe, IFocusGroup focuses) {
        builder.addDrawable(animatedFlame, 3, 29);
        builder.addDrawable(animatedFlame, 51, 29);
    }

    @Override
    public void draw(RecipeHolder<AlloySmeltingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX,
        double mouseY) {
        background.draw(guiGraphics);
    }
}
