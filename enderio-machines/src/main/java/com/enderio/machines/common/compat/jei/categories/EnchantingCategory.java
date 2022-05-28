package com.enderio.machines.common.compat.jei.categories;

import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.compat.jei.JEIPlugin;
import com.enderio.machines.common.compat.jei.helpers.EnchanterRecipeDisplayData;
import com.enderio.machines.common.compat.jei.helpers.EnchanterRecipeWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class EnchantingCategory implements IRecipeCategory<EnchanterRecipeWrapper> {
    private final IDrawable background;
    private final IDrawable icon;
    private final LoadingCache<EnchanterRecipeWrapper, EnchanterRecipeDisplayData> cachedDisplayData;

    public EnchantingCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(EnchanterScreen.BG_TEXTURE, 15, 34 - 12, 146, 18 + 12 + 16);
        icon = guiHelper.createDrawableIngredient(new ItemStack(MachineBlocks.ENCHANTER.get()));
        cachedDisplayData = CacheBuilder.newBuilder()
            .maximumSize(25)
            .build(new CacheLoader<>() {
                @Override
                public EnchanterRecipeDisplayData load(EnchanterRecipeWrapper key) {
                    return new EnchanterRecipeDisplayData();
                }
            });
    }

    @Override
    public ResourceLocation getUid() {
        return JEIPlugin.CATEGORY_ENCHANTING;
    }

    @Override
    public Class<? extends EnchanterRecipeWrapper> getRecipeClass() {
        return EnchanterRecipeWrapper.class;
    }

    @Override
    public Component getTitle() {
        return MachineBlocks.ENCHANTER.get().getName();
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
    public void setIngredients(EnchanterRecipeWrapper recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM_STACK, recipe.getInputs());
        ingredients.setOutput(VanillaTypes.ITEM_STACK, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, EnchanterRecipeWrapper recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 0, 12);
        guiItemStacks.init(1, true, 49, 12);
        guiItemStacks.init(2, true, 69, 12);
        guiItemStacks.init(3, false, 128, 12);

        guiItemStacks.set(ingredients);

        EnchanterRecipeDisplayData displayData = cachedDisplayData.getUnchecked(recipe);
        displayData.setCurrentIngredients(guiItemStacks.getGuiIngredients());
    }

    @Override
    public void draw(EnchanterRecipeWrapper recipe, PoseStack stack, double mouseX, double mouseY) {
        // TOdo; draw enchant name
        EnchanterRecipeDisplayData displayData = cachedDisplayData.getUnchecked(recipe);
        Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients = displayData.getCurrentIngredients();
        if (currentIngredients == null) {
            return;
        }

        ItemStack newLeftStack = currentIngredients.get(1).getDisplayedIngredient();
        ItemStack newRightStack = currentIngredients.get(2).getDisplayedIngredient();

        Minecraft minecraft = Minecraft.getInstance();

        Enchantment enchantment = recipe.getRecipe().getEnchantment();
        ItemStack result = currentIngredients.get(3).getDisplayedIngredient();

        if (result != null) {
            ListTag enchantments = EnchantedBookItem.getEnchantments(result);
            if (!enchantments.isEmpty()) {
                int level = EnchantmentHelper.getEnchantmentLevel(enchantments.getCompound(0));
                Component text = enchantment.getFullname(level);
                minecraft.font.draw(stack, text, 146 - minecraft.font.width(text), 0, 0xFF8B8B8B);
            }
        }

        if (newLeftStack == null || newRightStack == null) {
            return;
        }

        ItemStack lastLeftStack = displayData.getLastLeftStack();
        ItemStack lastRightStack = displayData.getLastRightStack();
        int lastCost = displayData.getLastCost();
        if (lastLeftStack == null || lastRightStack == null
            || !ItemStack.matches(lastLeftStack, newLeftStack)
            || !ItemStack.matches(lastRightStack, newRightStack)) {
            lastCost = recipe.getLevelCost(newLeftStack, newRightStack);
            displayData.setLast(newLeftStack, newRightStack, lastCost);
        }

        if (lastCost != 0) {
            String costText = lastCost < 0 ? "err" : Integer.toString(lastCost);
            String text = I18n.get("container.repair.cost", costText);

            int mainColor = 0xFF80FF20;
            LocalPlayer player = minecraft.player;
            if (player != null &&
                (lastCost >= 40 || lastCost > player.experienceLevel) &&
                !player.isCreative()) {
                // Show red if the player doesn't have enough levels
                mainColor = 0xFFFF6060;
            }

            drawRepairCost(minecraft, stack, text, mainColor);
        }
    }

    private void drawRepairCost(Minecraft minecraft, PoseStack poseStack, String text, int mainColor) {
        int shadowColor = 0xFF000000 | (mainColor & 0xFCFCFC) >> 2;
        int width = minecraft.font.width(text);
        int x = background.getWidth() - 2 - width;
        int y = 27 + 12;

        // TODO 1.13 match the new GuiRepair style
        minecraft.font.draw(poseStack, text, x + 1, y, shadowColor);
        minecraft.font.draw(poseStack, text, x, y + 1, shadowColor);
        minecraft.font.draw(poseStack, text, x + 1, y + 1, shadowColor);
        minecraft.font.draw(poseStack, text, x, y, mainColor);
    }
}