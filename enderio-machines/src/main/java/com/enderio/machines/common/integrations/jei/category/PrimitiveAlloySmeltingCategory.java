package com.enderio.machines.common.integrations.jei.category;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY;

import com.enderio.EnderIOBase;
import com.enderio.base.common.integrations.jei.JEIUtils;
import com.enderio.machines.client.gui.screen.PrimitiveAlloySmelterScreen;
import com.enderio.machines.client.gui.screen.StirlingGeneratorScreen;
import com.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.integrations.jei.util.RecipeUtil;
import com.enderio.machines.common.lang.MachineLang;
import java.util.Arrays;
import java.util.List;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

// TODO: Not a massive fan of how the primitive alloy smelter has been implemented and the resulting complexity...
public class PrimitiveAlloySmeltingCategory extends MachineRecipeCategory<RecipeHolder<AlloySmeltingRecipe>> {

    public static final RecipeType<RecipeHolder<AlloySmeltingRecipe>> TYPE = JEIUtils
            .createRecipeType(EnderIOBase.REGISTRY_NAMESPACE, "primitive_alloy_smelting", AlloySmeltingRecipe.class);

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
    public @Nullable ResourceLocation getRegistryName(RecipeHolder<AlloySmeltingRecipe> recipe) {
        // Prevent overlap with normal alloy smelter recipes
        return ResourceLocation.fromNamespaceAndPath(recipe.id().getNamespace(), "primitive_" + recipe.id().getPath());
    }

    @Override
    public RecipeType<RecipeHolder<AlloySmeltingRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlloySmeltingRecipe> recipe, IFocusGroup focuses) {
        List<SizedIngredient> inputs = recipe.value().inputs();

        if (!inputs.isEmpty()) {
            builder.addSlot(INPUT, 1, 1).addItemStacks(Arrays.stream(inputs.get(0).getItems()).toList());
        }

        if (inputs.size() > 1) {
            builder.addSlot(INPUT, 21, 1).addItemStacks(Arrays.stream(inputs.get(1).getItems()).toList());
        } else {
            builder.addSlot(RENDER_ONLY, 21, 1);
        }

        if (inputs.size() > 2) {
            builder.addSlot(INPUT, 41, 1).addItemStacks(Arrays.stream(inputs.get(2).getItems()).toList());
        } else {
            builder.addSlot(RENDER_ONLY, 41, 1);
        }

        builder.addSlot(OUTPUT, 97, 19).addItemStacks(List.of(RecipeUtil.getResultStacks(recipe).get(0).getItem()));

        builder.addSlot(RENDER_ONLY, 21, 37);
    }

    @Override
    public void draw(RecipeHolder<AlloySmeltingRecipe> recipe, IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics, double mouseX, double mouseY) {
        animatedFlame.draw(guiGraphics, 22, 20);

        // TODO: Draw time to smelt
    }
}
