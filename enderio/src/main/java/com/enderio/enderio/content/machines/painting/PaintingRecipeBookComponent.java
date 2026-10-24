package com.enderio.enderio.content.machines.painting;

import com.enderio.enderio.content.machines.slicer.SlicerMenu;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.content.paint.BlockPaintData;
import com.enderio.enderio.content.paint.item.PaintedBlockItem;
import com.enderio.enderio.foundation.recipe.PaintIngredient;
import com.enderio.enderio.foundation.recipe.PaintSlotDisplay;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeBookCategories;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.DisplaySlot;

import java.util.ArrayList;
import java.util.List;

public class PaintingRecipeBookComponent extends RecipeBookComponent<PaintingMachineMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );

    private static final List<TabInfo> TAB = List.of(
        new TabInfo(Items.DYE.red(), EIORecipeBookCategories.PAINTING.get())
    );

    public PaintingRecipeBookComponent(PaintingMachineMenu menu) {
        super(menu, TAB);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return slot.index == 1 || slot.index == 2 || slot.index == 3;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
        collection.selectRecipes(stackedContents, display -> display instanceof PaintingRecipe.PaintingDisplay);
    }

    @Override
    protected Component getRecipeFilterName() {
        return Component.translatable("gui.recipebook.toggleRecipes.craftable");
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipe, ContextMap context) {
        if (!(recipe instanceof PaintingRecipe.PaintingDisplay painting)) {
            return;
        }

        var input = painting.ingredient().resolveForFirstStack(context).getItem() instanceof PaintedBlockItem;
        ghostSlots.setInput(menu.getSlot(1), context, input ? painting.result() : painting.ingredient());
        ghostSlots.setInput(menu.getSlot(2), context, PaintSlotDisplay.INSTANCE);
        ghostSlots.setResult(menu.getSlot(3), context, painting.result());
    }
}
