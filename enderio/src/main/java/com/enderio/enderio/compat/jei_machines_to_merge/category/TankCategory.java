package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.FluidTankScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import com.enderio.enderio.foundation.util.SizedFluidIngredientHelper;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.forge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

// TODO: 1.20.1+ Add a custom TankRecipe for JEI to show mending and maybe fill/empty too.
public class TankCategory implements IRecipeCategory<TankRecipe> {
    public static final RecipeType<TankRecipe> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID, "tank",
            TankRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public TankCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(FluidTankScreen.BG_TEXTURE, 41, 18, 94, 53);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.FLUID_TANK.get()));
    }

    @Override
    public RecipeType<TankRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.TANK_TITLE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, TankRecipe recipe, IFocusGroup focuses) {
        List<FluidStack> fluidStacks = SizedFluidIngredientHelper.getFluidStacksInPreferredOrder(recipe.fluid());

        if (recipe.mode() == TankRecipe.Mode.EMPTY) {
            builder.addSlot(RecipeIngredientRole.INPUT, 3, 3).addIngredients(recipe.input());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 3, 34).addItemStack(recipe.output().copy());

            // Convert SizedFluidIngredient to FluidStack list for JEI display
            builder.addSlot(RecipeIngredientRole.OUTPUT, 39, 3)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, fluidStacks)
                    .setFluidRenderer(FluidTankBlockEntity.Standard.CAPACITY, false, 16, 47);
        } else if (recipe.mode() == TankRecipe.Mode.FILL) {
            builder.addSlot(RecipeIngredientRole.INPUT, 75, 3).addIngredients(recipe.input());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 34).addItemStack(recipe.output().copy());

            // Convert SizedFluidIngredient to FluidStack list for JEI display
            builder.addSlot(RecipeIngredientRole.INPUT, 39, 3)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, fluidStacks)
                    .setFluidRenderer(FluidTankBlockEntity.Standard.CAPACITY, false, 16, 47);
        }
    }
}
