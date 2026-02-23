package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import com.enderio.enderio.foundation.util.SizedFluidIngredientHelper;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

// TODO: 1.20.1+ Add a custom TankRecipe for JEI to show mending and maybe fill/empty too.
public class TankCategory implements IRecipeCategory<RecipeHolder<TankRecipe>> {
    public static final IRecipeType<RecipeHolder<TankRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID, "tank",
            TankRecipe.class);

    private final IDrawable icon;

    public TankCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.FLUID_TANK.get()));
    }

    @Override
    public IRecipeType<RecipeHolder<TankRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.TANK_TITLE;
    }

    @Override
    public int getWidth() {
        return 94;
    }

    @Override
    public int getHeight() {
        return 53;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TankRecipe> recipe, IFocusGroup focuses) {
        List<FluidStack> fluidStacks = SizedFluidIngredientHelper.getFluidStacksInPreferredOrder(recipe.value().fluid())
            .stream()
            .map(fluid -> new FluidStack(fluid.value(), recipe.value().fluid().amount()))
            .toList();

        if (recipe.value().mode() == TankRecipe.Mode.EMPTY) {
            builder.addSlot(RecipeIngredientRole.INPUT, 3, 3).add(recipe.value().input());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 3, 34).add(recipe.value().output().create());

            // Convert SizedFluidIngredient to FluidStack list for JEI display
            builder.addSlot(RecipeIngredientRole.OUTPUT, 39, 3)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, fluidStacks)
                    .setFluidRenderer(FluidTankBlockEntity.Standard.CAPACITY, false, 16, 47);
        } else if (recipe.value().mode() == TankRecipe.Mode.FILL) {
            builder.addSlot(RecipeIngredientRole.INPUT, 75, 3).add(recipe.value().input());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 34).add(recipe.value().output().create());

            // Convert SizedFluidIngredient to FluidStack list for JEI display
            builder.addSlot(RecipeIngredientRole.INPUT, 39, 3)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, fluidStacks)
                    .setFluidRenderer(FluidTankBlockEntity.Standard.CAPACITY, false, 16, 47);
        }
    }
}
