package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.integrations.jei.util.WrappedEnchanterRecipe;
import com.enderio.machines.common.lang.MachineLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class EnchanterCategory extends MachineRecipeCategory<WrappedEnchanterRecipe> {

    public static final RecipeType<WrappedEnchanterRecipe> TYPE = RecipeType.create(EnderIO.MODID, "enchanter", WrappedEnchanterRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public EnchanterCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(EnchanterScreen.BG_TEXTURE, 15, 24, 146, 28 + 12);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.ENCHANTER.get()));
    }

    @Override
    public RecipeType<WrappedEnchanterRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_ENCHANTER;
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
    public void setRecipe(IRecipeLayoutBuilder builder, WrappedEnchanterRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(INPUT, 1, 11)
            .addItemStack(new ItemStack(Items.WRITABLE_BOOK));

        builder.addSlot(INPUT, 50, 11)
            .addItemStacks(recipe.getInputs());
        builder.addSlot(INPUT, 70, 11)
            .addItemStacks(recipe.getLapis());

        builder.addSlot(OUTPUT, 129, 11)
            .addItemStack(recipe.getBook());
    }

    @Override
    public void draw(WrappedEnchanterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        Component title = recipe.getEnchantment().getFullname(recipe.getLevel());

        guiGraphics.drawString(mc.font, title, 146 - mc.font.width(title), 0, 0xff8b8b8b, false);

        int cost = recipe.getCost();
        String costText = cost < 0 ? "err" : Integer.toString(cost);
        String text = I18n.get("container.repair.cost", costText);

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        // Show red if the player doesn't have enough levels
        int mainColor = playerHasEnoughLevels(player, cost) ? 0xFF80FF20 : 0xFFFF6060;
        int repairTextWidth = minecraft.font.width(text);
        guiGraphics.drawString(minecraft.font, text, background.getWidth() - 2 - repairTextWidth, background.getHeight() - 8, mainColor);
    }
}
