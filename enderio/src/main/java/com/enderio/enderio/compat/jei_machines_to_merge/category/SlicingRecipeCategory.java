package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.compat.jei_machines_to_merge.util.RecipeUtil;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.*;

public class SlicingRecipeCategory extends MachineRecipeCategory<SlicingRecipe> {

    public static final RecipeType<SlicingRecipe> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID,
            "slicing", SlicingRecipe.class);

    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/viewer/slice_and_splice.png");
    private static final int WIDTH = 108;
    private static final int HEIGHT = 60;

    private final IDrawable background;
    private final IDrawable icon;

    public SlicingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.SLICE_AND_SPLICE.get()));
    }

    @Override
    public RecipeType<SlicingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.SLICING_TITLE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, SlicingRecipe recipe, IFocusGroup focuses) {
        // Tool slots
        builder.addSlot(CATALYST, 11, 1).addIngredients(Ingredient.of(ItemTags.AXES));
        builder.addSlot(CATALYST, 29, 1).addIngredients(Ingredient.of(Tags.Items.TOOLS_SHEAR));

        builder.addSlot(INPUT, 1, 25).addIngredients(recipe.inputs().get(0));
        builder.addSlot(INPUT, 19, 25).addIngredients(recipe.inputs().get(1));
        builder.addSlot(INPUT, 37, 25).addIngredients(recipe.inputs().get(2));
        builder.addSlot(INPUT, 1, 43).addIngredients(recipe.inputs().get(3));
        builder.addSlot(INPUT, 19, 43).addIngredients(recipe.inputs().get(4));
        builder.addSlot(INPUT, 37, 43).addIngredients(recipe.inputs().get(5));

        // Output
        builder.addSlot(OUTPUT, 91, 34).addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));
    }
}
