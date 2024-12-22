package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIOBase;
import com.enderio.base.common.integrations.jei.JEIUtils;
import com.enderio.machines.client.gui.screen.FluidTankScreen;
import com.enderio.machines.common.blocks.fluid_tank.FluidTankBlockEntity;
import com.enderio.machines.common.blocks.fluid_tank.TankRecipe;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

// TODO: 1.20.1+ Add a custom TankRecipe for JEI to show mending and maybe fill/empty too.
public class TankCategory implements IRecipeCategory<RecipeHolder<TankRecipe>> {
    public static final RecipeType<RecipeHolder<TankRecipe>> TYPE = JEIUtils
            .createRecipeType(EnderIOBase.REGISTRY_NAMESPACE, "tank", TankRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public TankCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(FluidTankScreen.BG_TEXTURE, 41, 18, 94, 53);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.FLUID_TANK.get()));
    }

    @Override
    public RecipeType<RecipeHolder<TankRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_TANK;
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TankRecipe> recipe, IFocusGroup focuses) {

        if (recipe.value().mode() == TankRecipe.Mode.EMPTY) {
            builder.addSlot(RecipeIngredientRole.INPUT, 3, 3).addIngredients(recipe.value().input());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 3, 34).addItemStack(recipe.value().output().copy());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 39, 3)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.value().fluid()))
                    .setFluidRenderer(FluidTankBlockEntity.Standard.CAPACITY, false, 16, 47);
        } else if (recipe.value().mode() == TankRecipe.Mode.FILL) {
            builder.addSlot(RecipeIngredientRole.INPUT, 75, 3).addIngredients(recipe.value().input());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 34).addItemStack(recipe.value().output().copy());

            builder.addSlot(RecipeIngredientRole.INPUT, 39, 3)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.value().fluid()))
                    .setFluidRenderer(FluidTankBlockEntity.Standard.CAPACITY, false, 16, 47);
        }
    }
}
