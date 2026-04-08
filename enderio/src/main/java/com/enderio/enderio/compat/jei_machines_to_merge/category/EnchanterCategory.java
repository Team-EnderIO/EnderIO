package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.EnchanterScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.compat.jei_machines_to_merge.util.WrappedEnchanterRecipe;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.Nullable;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class EnchanterCategory extends MachineRecipeCategory<WrappedEnchanterRecipe> {

    public static final IRecipeType<WrappedEnchanterRecipe> TYPE = IRecipeType.create(EnderIO.MOD_ID, "enchanter",
            WrappedEnchanterRecipe.class);

    private final IDrawableStatic background;
    private final IDrawable icon;

    public EnchanterCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(EnchanterScreen.BG_TEXTURE, 15, 24, 146, 28 + 12);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.ENCHANTER.get()));
    }

    @Override
    public @Nullable Identifier getIdentifier(WrappedEnchanterRecipe recipe) {
        return recipe.id();
    }


    @Override
    public IRecipeType<WrappedEnchanterRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.ENCHANTER_TITLE;
    }

    @Override
    public int getWidth() {
        return 146;
    }

    @Override
    public int getHeight() {
        return 28+12;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WrappedEnchanterRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(INPUT, 1, 11).add(new ItemStack(Items.WRITABLE_BOOK));

        builder.addSlot(INPUT, 50, 11).addItemStacks(recipe.getInputs());
        builder.addSlot(INPUT, 70, 11).addItemStacks(recipe.getLapis());

        builder.addSlot(OUTPUT, 129, 11).add(recipe.getBook());
    }

    @Override
    public void draw(WrappedEnchanterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics,
            double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        Component title = Enchantment.getFullname(recipe.getEnchantment(), recipe.getLevel());

        graphics.text(mc.font, title, 146 - mc.font.width(title), 0, 0xff8b8b8b, false);

        int cost = recipe.getCost();
        String costText = cost < 0 ? "err" : Integer.toString(cost);
        String text = I18n.get("container.repair.cost", costText);

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        // Show red if the player doesn't have enough levels
        int mainColor = playerHasEnoughLevels(player, cost) ? 0xFF80FF20 : 0xFFFF6060;
        int repairTextWidth = minecraft.font.width(text);
        graphics.text(minecraft.font, text, getWidth() - 2 - repairTextWidth,
                getHeight() - 8, mainColor);
    }
}
