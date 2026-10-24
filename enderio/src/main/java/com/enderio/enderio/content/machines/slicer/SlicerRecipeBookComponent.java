package com.enderio.enderio.content.machines.slicer;

import com.enderio.enderio.init.EIORecipeBookCategories;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.List;

public class SlicerRecipeBookComponent extends RecipeBookComponent<SlicerMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );

    private static final List<TabInfo> TAB = List.of(
        new TabInfo(Items.IRON_AXE, EIORecipeBookCategories.SLICING.get())
    );

    public SlicerRecipeBookComponent(SlicerMenu menu) {
        super(menu, TAB);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        int index = slot.index;
        return index >= SlicerMenu.INPUTS_INDEX && index < (SlicerMenu.INPUTS_INDEX + SlicerMenu.INPUT_COUNT);
    }

    @Override
    protected Component getRecipeFilterName() {
        return Component.translatable("gui.recipebook.toggleRecipes.craftable");
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
        if (!(display instanceof SlicingRecipe.SlicingDisplay slicing)) {
            return;
        }

        for (int i = 0; i < slicing.ingredients().size(); i++) {
            ghostSlots.setInput(menu.getSlot(i + SlicerMenu.INPUTS_INDEX), context, slicing.ingredients().get(i));
        }

        ghostSlots.setResult(menu.getSlot(SlicerMenu.INPUTS_INDEX + SlicerMenu.INPUT_COUNT), context, slicing.result());
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
        collection.selectRecipes(stackedContents, display -> display instanceof SlicingRecipe.SlicingDisplay);
    }
}
