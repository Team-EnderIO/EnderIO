package com.enderio.machines.common.integrations.jei.transfer;

import com.enderio.machines.common.blocks.fluid_tank.FluidTankMenu;
import com.enderio.machines.common.blocks.fluid_tank.TankRecipe;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.integrations.jei.category.TankCategory;
import com.enderio.machines.common.network.TransferItemsPacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidTankTransferHelper implements IRecipeTransferHandler<FluidTankMenu, RecipeHolder<TankRecipe>> {

    private final IRecipeTransferHandlerHelper handlerHelper;

    public FluidTankTransferHelper(IRecipeTransferHandlerHelper handlerHelper) {
        this.handlerHelper = handlerHelper;
    }

    @Override
    public Class<? extends FluidTankMenu> getContainerClass() {
        return FluidTankMenu.class;
    }

    @Override
    public Optional<MenuType<FluidTankMenu>> getMenuType() {
        return Optional.of(MachineMenus.FLUID_TANK.get());
    }

    @Override
    public RecipeType<RecipeHolder<TankRecipe>> getRecipeType() {
        return TankCategory.TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(FluidTankMenu container, RecipeHolder<TankRecipe> recipe, IRecipeSlotsView recipeSlots, Player player,
        boolean maxTransfer, boolean doTransfer) {

        boolean hasFluid = true;
        if (recipe.value().mode() == TankRecipe.Mode.FILL) {
            hasFluid = recipe.value().fluid().getFluid().isSame(container.getFluidTank().contents().getFluid()) && recipe.value().fluid().getAmount() <= container.getFluidTank().contents().getAmount();
        }

        boolean hasItem = recipe.value().input().test(container.slots.get(0).getItem());
        Ingredient item = getIngredientItem(player, recipe.value().input());

        if (!hasItem || !hasFluid) {
            Component message = Component.translatable("jei.tooltip.error.recipe.transfer.missing");
            List<IRecipeSlotView> empty = new ArrayList<>();
            if (!hasItem && item.isEmpty()) {
                empty.add(recipeSlots.getSlotViews().get(0));
            }
            if (!hasFluid) {
                empty.add(recipeSlots.getSlotViews().get(2));
            }
            if (!empty.isEmpty()) {
                return handlerHelper.createUserErrorForMissingSlots(message, empty);
            }
        }

        if (doTransfer) {
            List<Ingredient> toSend = NonNullList.withSize(4, Ingredient.EMPTY);
            if (recipe.value().mode() == TankRecipe.Mode.EMPTY) {
                toSend.set(0, item);
            } else {
                toSend.set(2, item);
            }
            PacketDistributor.sendToServer(new TransferItemsPacket(toSend, 0,3 + 1, maxTransfer));
        }
        return null;
    }

    private Ingredient getIngredientItem(Player player, Ingredient ingredient) {
        for (var item : player.getInventory().items) {
            if (ingredient.test(item)) {
                return ingredient;
            }
        }
        return Ingredient.EMPTY;
    }
}
