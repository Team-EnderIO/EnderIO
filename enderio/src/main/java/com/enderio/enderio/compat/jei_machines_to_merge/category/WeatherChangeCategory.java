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
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class WeatherChangeCategory extends MachineRecipeCategory<RecipeHolder<WeatherChangeRecipe>> {

    public static final IRecipeType<RecipeHolder<WeatherChangeRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID, "weather_change",
        WeatherChangeRecipe.class);

    private final IDrawable icon;
    private final IDrawableStatic staticFlame;
    private final IDrawable animatedFlame;

    public WeatherChangeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.WEATHER_OBELISK.get()));

        staticFlame = guiHelper.createDrawable(WeatherObeliskScreen.WEATHER_BG, 176, 0, 12, 32);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 600, IDrawableAnimated.StartDirection.BOTTOM,
            false);
    }

    @Override
    public IRecipeType<RecipeHolder<WeatherChangeRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.WEATHER_CHANGE_TITLE;
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 76;
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
            .add(recipe.value().fluid().fluid().value(), recipe.value().fluid().amount())
            .setFluidRenderer(WeatherObeliskBlockEntity.TANK_CAPACITY, false, 16, 63);
    }

    // TODO: 26.1 - reenable
//    @Override
//    public void draw(RecipeHolder<WeatherChangeRecipe> recipe, IRecipeSlotsView recipeSlotsView,
//        GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
//        animatedFlame.draw(graphics, 63, 24);
//    }
}
