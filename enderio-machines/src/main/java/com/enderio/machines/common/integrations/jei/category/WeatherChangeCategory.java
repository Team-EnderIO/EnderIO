package com.enderio.machines.common.integrations.jei.category;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.integrations.jei.JEIUtils;
import com.enderio.machines.client.gui.screen.WeatherObeliskScreen;
import com.enderio.machines.common.blocks.obelisks.weather.WeatherChangeRecipe;
import com.enderio.machines.common.blocks.obelisks.weather.WeatherObeliskBlockEntity;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.lang.MachineLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeatherChangeCategory extends MachineRecipeCategory<RecipeHolder<WeatherChangeRecipe>> {

    public static final RecipeType<RecipeHolder<WeatherChangeRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.NAMESPACE, "weather_change",
        WeatherChangeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic staticFlame;
    private final IDrawable animatedFlame;

    public WeatherChangeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(WeatherObeliskScreen.WEATHER_BG, 18, 4, 120, 76);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.WEATHER_OBELISK.get()));

        staticFlame = guiHelper.createDrawable(WeatherObeliskScreen.WEATHER_BG, 176, 0, 12, 32);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 600, IDrawableAnimated.StartDirection.BOTTOM,
            false);
    }

    @Override
    public RecipeType<RecipeHolder<WeatherChangeRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_WEATHER_CHANGE;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<WeatherChangeRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 62, 7)
            .addItemStacks(List.of(new ItemStack(Items.FIREWORK_ROCKET)));

        builder.addSlot(RecipeIngredientRole.INPUT, 4, 7)
            .addFluidStack(recipe.value().fluid().getFluid(), recipe.value().fluid().getAmount())
            .setFluidRenderer(WeatherObeliskBlockEntity.TANK_CAPACITY, false, 16, 63);
    }

    @Override
    public void draw(RecipeHolder<WeatherChangeRecipe> recipe, IRecipeSlotsView recipeSlotsView,
        GuiGraphics guiGraphics, double mouseX, double mouseY) {
        animatedFlame.draw(guiGraphics, 63, 24);
    }
}
