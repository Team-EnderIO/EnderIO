package com.enderio.machines.common.integrations.jei.transfer;

import com.enderio.machines.common.blocks.crafter.CrafterMenu;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.network.UpdateCrafterTemplatePacket;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CrafterRecipeTransferHandler implements IRecipeTransferHandler<CrafterMenu, RecipeHolder<CraftingRecipe>> {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final IRecipeTransferHandlerHelper handlerHelper;

    public CrafterRecipeTransferHandler(IRecipeTransferHandlerHelper handlerHelper) {
        this.handlerHelper = handlerHelper;
    }

    @Override
    public Class<? extends CrafterMenu> getContainerClass() {
        return CrafterMenu.class;
    }

    @Override
    public Optional<MenuType<CrafterMenu>> getMenuType() {
        return Optional.of(MachineMenus.CRAFTER.get());
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(CrafterMenu container, RecipeHolder<CraftingRecipe> recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {

        List<ItemStack> placedStacks = new ArrayList<>();
        var ingredients = recipe.value().getIngredients();
        if (recipe.value() instanceof ShapedRecipe shapedRecipe) {
            // Order matters, this makes indices go in 1,2,3 order.
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    int ingredientIndex = x + y * shapedRecipe.getWidth();

                    if (x >= shapedRecipe.getWidth() || y >= shapedRecipe.getHeight()) {
                        placedStacks.add(ItemStack.EMPTY);
                        continue;
                    }

                    var ingredient = ingredients.get(ingredientIndex);

                    if (ingredient.isEmpty()) {
                        placedStacks.add(ItemStack.EMPTY);
                    } else {
                        placedStacks.add(getIngredientItem(player, ingredient));
                    }
                }
            }
        } else if (recipe.value() instanceof ShapelessRecipe) {
            // order still matters
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    int ingredientIndex = x + y * 3;

                    if (ingredientIndex < ingredients.size()) {
                        placedStacks.add(getIngredientItem(player, ingredients.get(ingredientIndex)));
                    } else {
                        placedStacks.add(ItemStack.EMPTY);
                    }
                }
            }
        } else {
            LOGGER.warn("JEI Failure: tried to use a non shaped or shapeless recipe with crafter: "
                    + recipe.getClass().getName());
            return handlerHelper.createInternalError();
        }

        if (doTransfer) {
            PacketDistributor.sendToServer(new UpdateCrafterTemplatePacket(placedStacks));
        }

        return null;
    }

    private ItemStack getIngredientItem(Player player, Ingredient ingredient) {
        for (var item : player.getInventory().items) {
            if (ingredient.test(item)) {
                return new ItemStack(item.getItem(), 1);
            }
        }

        return ingredient.getItems()[0];
    }
}
