package com.enderio.machines.common.integrations.jei.transfer;

import com.enderio.machines.common.blocks.obelisks.weather.WeatherChangeRecipe;
import com.enderio.machines.common.blocks.obelisks.weather.WeatherObeliskMenu;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.integrations.jei.category.WeatherChangeCategory;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeatherObeliskTransferHelper implements IRecipeTransferHandler<WeatherObeliskMenu, RecipeHolder<WeatherChangeRecipe>> {

    private final IRecipeTransferHandlerHelper handlerHelper;

    public WeatherObeliskTransferHelper(IRecipeTransferHandlerHelper handlerHelper) {
        this.handlerHelper = handlerHelper;
    }

    @Override
    public Class<? extends WeatherObeliskMenu> getContainerClass() {
        return WeatherObeliskMenu.class;
    }

    @Override
    public Optional<MenuType<WeatherObeliskMenu>> getMenuType() {
        return Optional.of(MachineMenus.WEATHER_OBELISK.get());
    }

    @Override
    public RecipeType<RecipeHolder<WeatherChangeRecipe>> getRecipeType() {
        return WeatherChangeCategory.TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(WeatherObeliskMenu container, RecipeHolder<WeatherChangeRecipe> recipe, IRecipeSlotsView recipeSlots,
        Player player, boolean maxTransfer, boolean doTransfer) {
        boolean hasFluid = recipe.value().fluid().getFluid().isSame(container.getFluidTank().contents().getFluid()) && recipe.value().fluid().getAmount() <= container.getFluidTank().contents().getAmount();
        boolean hasFireWork = container.slots.get(WeatherObeliskMenu.INPUTS_INDEX).getItem().is(Items.FIREWORK_ROCKET);

        ItemStack fireWork = getIngredientItem(player);

        if (!hasFireWork || !hasFluid) {
            Component message = Component.translatable("jei.tooltip.error.recipe.transfer.missing");
            List<IRecipeSlotView> empty = new ArrayList<>();
            if (!hasFireWork && fireWork.isEmpty()) {
                empty.add(recipeSlots.getSlotViews().get(WeatherObeliskMenu.INPUTS_INDEX));
            }
            if (!hasFluid) {
                empty.add(recipeSlots.getSlotViews().get(WeatherObeliskMenu.INPUTS_INDEX + 1));
            }
            if (!empty.isEmpty()) {
                return handlerHelper.createUserErrorForMissingSlots(message, empty);
            }
        }

        if (doTransfer) {
            PacketDistributor.sendToServer(new TransferItemsPacket(List.of(Ingredient.of(fireWork)), WeatherObeliskMenu.INPUTS_INDEX,WeatherObeliskMenu.LAST_INDEX + 1, maxTransfer));
        }
        return null;
    }

    private ItemStack getIngredientItem(Player player) {
        for (var item : player.getInventory().items) {
            if (item.is(Items.FIREWORK_ROCKET)) {
                return new ItemStack(item.getItem(), 1);
            }
        }
        return ItemStack.EMPTY;
    }
}
