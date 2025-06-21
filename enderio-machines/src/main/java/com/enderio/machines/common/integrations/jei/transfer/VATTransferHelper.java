package com.enderio.machines.common.integrations.jei.transfer;

import com.enderio.machines.common.blocks.vat.FermentingRecipe;
import com.enderio.machines.common.blocks.vat.VatMenu;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.integrations.jei.category.VATCategory;
import com.enderio.machines.common.network.TransferItemsPacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VATTransferHelper implements IRecipeTransferHandler<VatMenu, RecipeHolder<FermentingRecipe>> {

    private final IRecipeTransferHandlerHelper handlerHelper;

    public VATTransferHelper(IRecipeTransferHandlerHelper handlerHelper) {
        this.handlerHelper = handlerHelper;
    }

    @Override
    public Class<? extends VatMenu> getContainerClass() {
        return VatMenu.class;
    }

    @Override
    public Optional<MenuType<VatMenu>> getMenuType() {
        return Optional.of(MachineMenus.VAT.get());
    }

    @Override
    public RecipeType<RecipeHolder<FermentingRecipe>> getRecipeType() {
        return VATCategory.TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(VatMenu container, RecipeHolder<FermentingRecipe> recipe, IRecipeSlotsView recipeSlots, Player player,
        boolean maxTransfer, boolean doTransfer) {

        boolean hasFluid = recipe.value().input().ingredient().test(new FluidStack(container.getInputTank().contents().getFluid(), container.getInputTank().contents().getAmount()));
        boolean hasLeft = container.slots.get(VatMenu.INPUTS_INDEX).getItem().is(recipe.value().leftReagent());
        boolean hasRight = container.slots.get(VatMenu.INPUTS_INDEX + 1).getItem().is(recipe.value().rightReagent());
        Ingredient left = getIngredientItem(player, Ingredient.of(recipe.value().leftReagent()));
        Ingredient right = getIngredientItem(player, Ingredient.of(recipe.value().rightReagent()));

        if (!hasLeft || !hasRight || !hasFluid) {
            Component message = Component.translatable("jei.tooltip.error.recipe.transfer.missing");
            List<IRecipeSlotView> empty = new ArrayList<>();
            if (!hasLeft && left.isEmpty()) {
                empty.add(recipeSlots.getSlotViews().get(VatMenu.INPUTS_INDEX));
            }
            if (!hasRight && right.isEmpty()) {
                empty.add(recipeSlots.getSlotViews().get(VatMenu.INPUTS_INDEX + 1));
            }
            if (!hasFluid) {
                empty.add(recipeSlots.getSlotViews().get(VatMenu.INPUTS_INDEX + 2));
            }
            if (!empty.isEmpty()) {
                return handlerHelper.createUserErrorForMissingSlots(message, empty);
            }
        }

        if (doTransfer) {
            PacketDistributor.sendToServer(new TransferItemsPacket(List.of(left, right), VatMenu.INPUTS_INDEX,VatMenu.LAST_INDEX + 1, maxTransfer));
        }
        return null;
    }

    private Ingredient getIngredientItem(Player player, Ingredient ingredient) {
        for (var item : player.getInventory().items) {
            if (ingredient.test(item)) {
                return ingredient;
            }
        }
        return Ingredient.of(ItemStack.EMPTY);
    }
}
