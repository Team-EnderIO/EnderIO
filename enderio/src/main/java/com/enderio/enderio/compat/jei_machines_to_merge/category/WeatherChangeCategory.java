package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.WeatherObeliskScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherChangeRecipe;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherObeliskBlockEntity;
import com.enderio.enderio.init.EIOBlocks;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeatherChangeCategory extends MachineRecipeCategory<WeatherChangeRecipe> {

    public static final RecipeType<WeatherChangeRecipe> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID, "weather_change",
        WeatherChangeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic staticFlame;
    private final IDrawable animatedFlame;

    public WeatherChangeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(WeatherObeliskScreen.WEATHER_BG, 18, 4, 120, 76);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.WEATHER_OBELISK.get()));

        staticFlame = guiHelper.createDrawable(WeatherObeliskScreen.WEATHER_BG, 176, 0, 12, 32);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 600, IDrawableAnimated.StartDirection.BOTTOM,
            false);
    }

    @Override
    public RecipeType<WeatherChangeRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.WEATHER_CHANGE_TITLE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, WeatherChangeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 62, 7)
            .addItemStacks(List.of(new ItemStack(Items.FIREWORK_ROCKET)));

        builder.addSlot(RecipeIngredientRole.INPUT, 4, 7)
            .addFluidStack(recipe.fluid().getFluid(), recipe.fluid().getAmount())
            .setFluidRenderer(WeatherObeliskBlockEntity.TANK_CAPACITY, false, 16, 63);
    }

    @Override
    public void draw(WeatherChangeRecipe recipe, IRecipeSlotsView recipeSlotsView,
        GuiGraphics guiGraphics, double mouseX, double mouseY) {
        animatedFlame.draw(guiGraphics, 63, 24);
    }
}
