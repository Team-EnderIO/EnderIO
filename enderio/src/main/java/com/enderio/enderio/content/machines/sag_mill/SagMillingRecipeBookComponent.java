package com.enderio.enderio.content.machines.sag_mill;

import com.enderio.enderio.content.machines.slicer.SlicerMenu;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.init.EIOItems;
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
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.List;

public class SagMillingRecipeBookComponent extends RecipeBookComponent<SagMillMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );

    private static final List<TabInfo> TAB = List.of(
        new TabInfo(EIOItems.POWDERED_COAL.get(), EIORecipeBookCategories.SAG_MILL.get())
    );

    public SagMillingRecipeBookComponent(SagMillMenu menu) {
        super(menu, TAB);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        int index = slot.index;
        return index >= SagMillMenu.INPUTS_INDEX && index < (SagMillMenu.INPUTS_INDEX + SagMillMenu.INPUTS_INDEX);
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
        collection.selectRecipes(stackedContents, display -> display instanceof SagMillingRecipe.SagMillingDisplay);
    }

    @Override
    protected Component getRecipeFilterName() {
        return Component.translatable("gui.recipebook.toggleRecipes.craftable");
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
        if (!(display instanceof SagMillingRecipe.SagMillingDisplay sagmill)) {
            return;
        }

        ghostSlots.setInput(menu.getSlot(SagMillMenu.INPUTS_INDEX), context, sagmill.ingredient());
        ghostSlots.setResult(menu.getSlot(2), context, sagmill.result());
    }
}
