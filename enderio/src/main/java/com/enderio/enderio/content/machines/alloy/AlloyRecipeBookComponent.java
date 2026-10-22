package com.enderio.enderio.content.machines.alloy;

import com.enderio.core.common.recipes.EOIRecipeGhostSlots;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipeBookCategories;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.List;

public class AlloyRecipeBookComponent extends RecipeBookComponent<AlloySmelterMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
        Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );

    private static final List<TabInfo> TAB = List.of(
        //new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.valueOf("ENDERIO_ALLOY_SMELTING")),
        new TabInfo(EIOItems.DARK_STEEL_INGOT.get(), EIORecipeBookCategories.ALLOY_SMELTING.get()),
        new TabInfo(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
        new TabInfo(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
        new TabInfo(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC),
        new TabInfo(Items.REDSTONE_ORE, RecipeBookCategories.BLAST_FURNACE_BLOCKS),
        new TabInfo(Items.IRON_SHOVEL, Items.GOLDEN_LEGGINGS, RecipeBookCategories.BLAST_FURNACE_MISC),
        new TabInfo(Items.PORKCHOP, RecipeBookCategories.SMOKER_FOOD)
    );

    public AlloyRecipeBookComponent(AlloySmelterMenu menu) {
        super(menu, TAB);
        this.ghostSlots = new EOIRecipeGhostSlots(this.ghostSlots.slotSelectTime);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return slot == this.menu.getSlot(1) || slot == this.menu.getSlot(2) || slot == this.menu.getSlot(3)
            || slot == this.menu.getSlot(4);
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
        recipeCollection.selectRecipes(stackedItemContents, display -> display instanceof AlloySmeltingRecipe.AlloySmelterDisplay
            || display instanceof FurnaceRecipeDisplay);
    }

    @Override
    protected Component getRecipeFilterName() {
        return Component.translatable("gui.recipebook.toggleRecipes.craftable");
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {
        if (recipeDisplay instanceof AlloySmeltingRecipe.AlloySmelterDisplay alloying) {
            for (int i = 0; i < alloying.ingredients().size(); i++) {
                ghostSlots.setInput(this.menu.getSlot(i+1), contextMap, alloying.ingredients().get(i));
            }
            ghostSlots.setResult(this.menu.getSlot(4), contextMap, alloying.result());
        }

        if (recipeDisplay instanceof FurnaceRecipeDisplay furnace) {
            ghostSlots.setInput(this.menu.getSlot(1), contextMap, furnace.ingredient());
            ghostSlots.setResult(this.menu.getSlot(4), contextMap, furnace.result());
        }
    }
}
