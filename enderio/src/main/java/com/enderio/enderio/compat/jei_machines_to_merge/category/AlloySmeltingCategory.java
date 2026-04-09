package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.StirlingGeneratorScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.compat.jei_machines_to_merge.util.RecipeUtil;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.*;

public class AlloySmeltingCategory extends MachineRecipeCategory<RecipeHolder<AlloySmeltingRecipe>> {

    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/viewer/alloy_smelter.png");
    private static final int WIDTH = 67 + 40; // + 40 text space
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlloySmeltingRecipe> recipe, IFocusGroup focuses) {
        List<SizedIngredient> inputs = recipe.value().inputs();

        if (!inputs.isEmpty()) {
            builder.addSlot(INPUT, 1, 11).add(inputs.getFirst().ingredient());
        } else {
            builder.addSlot(RENDER_ONLY, 1, 11);
        }

        if (inputs.size() > 1) {
            builder.addSlot(INPUT, 26, 1).add(inputs.get(1).ingredient());
        } else {
            builder.addSlot(RENDER_ONLY, 26, 1);
        }

        if (inputs.size() > 2) {
            builder.addSlot(INPUT, 50, 11).add(inputs.get(2).ingredient());
        } else {
            builder.addSlot(RENDER_ONLY, 50, 11);
        }

        builder.addSlot(OUTPUT, 26, 52).addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).getFirst().getItem()));
    }

    @Override
    public void draw(RecipeHolder<AlloySmeltingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX,
        double mouseY) {
        background.draw(guiGraphics);
        animatedFlame.draw(guiGraphics, 3, 29);
        animatedFlame.draw(guiGraphics, 51, 29);
        guiGraphics.text(Minecraft.getInstance().font, getBasicEnergyString(recipe), 60, 50, 0xff808080, false);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<AlloySmeltingRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 60 && mouseY > 50 && mouseX < 60 + mc.font.width(getBasicEnergyString(recipe))
            && mouseY < 50 + mc.font.lineHeight) {
            tooltip.add(EIOCommonLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }
    }
}
