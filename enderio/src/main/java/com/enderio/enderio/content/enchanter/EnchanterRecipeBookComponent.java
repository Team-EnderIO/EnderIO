package com.enderio.enderio.content.enchanter;

import com.enderio.core.common.recipes.EOIRecipeGhostSlots;
import com.enderio.enderio.init.EIORecipeBookCategories;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.List;

public class EnchanterRecipeBookComponent extends RecipeBookComponent<EnchanterMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );

    private static final List<TabInfo> TAB = List.of(
        new TabInfo(Items.ENCHANTED_BOOK, EIORecipeBookCategories.ENCHANTING.get())
    );

    public EnchanterRecipeBookComponent(EnchanterMenu menu) {
        super(menu, TAB);
        this.ghostSlots = new EOIRecipeGhostSlots(this.ghostSlots.slotSelectTime);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(net.minecraft.world.inventory.Slot slot) {
        return slot == this.menu.getSlot(0) || slot == this.menu.getSlot(1) || slot == this.menu.getSlot(2)
            || slot == this.menu.getSlot(3);
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
        if (!(display instanceof EnchanterRecipe.EnchantingDisplay enchanting)) {
            return;
        }

        ghostSlots.setInput(this.menu.getSlot(0), context, enchanting.book());
        ghostSlots.setInput(this.menu.getSlot(1), context, enchanting.ingredient());
        ghostSlots.setInput(this.menu.getSlot(2), context, enchanting.lapis());
        ghostSlots.setResult(this.menu.getSlot(3), context, enchanting.result());
    }

    @Override
    protected Component getRecipeFilterName() {
        return Component.translatable("gui.recipebook.toggleRecipes.craftable");
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
        collection.selectRecipes(stackedContents, display -> display instanceof EnchanterRecipe.EnchantingDisplay);
    }
}
