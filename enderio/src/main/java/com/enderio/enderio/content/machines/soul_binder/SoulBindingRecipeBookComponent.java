package com.enderio.enderio.content.machines.soul_binder;

import com.enderio.core.common.recipes.EOIRecipeGhostSlots;
import com.enderio.enderio.content.machines.slicer.SlicerMenu;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.foundation.recipe.SoulIngredientSlotDisplay;
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
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public class SoulBindingRecipeBookComponent extends RecipeBookComponent<SoulBinderMenu> {

    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );

    private static final List<TabInfo> TAB = List.of(
        new TabInfo(EIOItems.SOUL_VIAL.get(), EIORecipeBookCategories.SOUL_BINDING.get())
    );

    public SoulBindingRecipeBookComponent(SoulBinderMenu menu) {
        super(menu, TAB);
        this.ghostSlots = new EOIRecipeGhostSlots(this.ghostSlots.slotSelectTime);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        int index = slot.index;
        return index >= SoulBinderMenu.INPUTS_INDEX && index < (SoulBinderMenu.INPUTS_INDEX + SoulBinderMenu.INPUT_COUNT);
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
        collection.selectRecipes(stackedContents, display -> display instanceof SoulBindingRecipe.SoulBindingDisplay);
    }

    @Override
    protected Component getRecipeFilterName() {
        return Component.translatable("gui.recipebook.toggleRecipes.craftable");
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
        if (!(display instanceof SoulBindingRecipe.SoulBindingDisplay soulBinding)) {
            return;
        }

        ghostSlots.setInput(menu.getSlot(SoulBinderMenu.INPUTS_INDEX), context, soulBinding.ingredient());
        ghostSlots.setInput(menu.getSlot(SoulBinderMenu.INPUTS_INDEX + 1), context, soulBinding.soul());
        ghostSlots.setResult(menu.getSlot(SoulBinderMenu.INPUTS_INDEX + SoulBinderMenu.INPUT_COUNT), context, soulBinding.result());
    }
}
