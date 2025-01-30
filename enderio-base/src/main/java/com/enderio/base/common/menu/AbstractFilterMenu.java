package com.enderio.base.common.menu;

import com.enderio.core.common.menu.BaseEnderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

// TODO: Test, but should provide everything we need to open the filters from the conduit UI.
public abstract class AbstractFilterMenu extends BaseEnderMenu {

    public static int BACK_BUTTON_ID = 0;

    private final FilterAccess filterAccess;

    /**
     * Server menu constructor
     */
    protected AbstractFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory);
        this.filterAccess = filterAccess;
    }

    /**
     * Client menu constructor.
     * Filter access should be created from the network buffer available in the client constructors.
     */
    protected AbstractFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,
        ClientFilterAccess filterAccess) {
        super(menuType, containerId, playerInventory);
        this.filterAccess = filterAccess;
    }

    protected ItemStack getFilterStack() {
        return filterAccess.getFilterItem();
    }

    @Override
    public boolean stillValid(Player player) {
        return filterAccess.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BACK_BUTTON_ID) {
            if (filterAccess.hasCustomBackDestination()) {
                filterAccess.goBack();
            } else {
                // Simply close the menu.
                this.getPlayerInventory().player.closeContainer();
            }
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    public sealed interface FilterAccess {
        ItemStack getFilterItem();
        boolean stillValid(Player player);
        boolean hasCustomBackDestination();
        void goBack();
    }

    protected static final class ClientFilterAccess implements FilterAccess {

        public ItemStack stack = ItemStack.EMPTY;

        @Override
        public ItemStack getFilterItem() {
            return stack;
        }

        @Override
        public boolean stillValid(Player player) {
            // TODO: Some way to verify on the client?
            return true;
        }

        @Override
        public boolean hasCustomBackDestination() {
            return false;
        }

        @Override
        public void goBack() {
        }
    }

    public record HandFilterAccess(ItemStack stack) implements FilterAccess {

        @Override
        public ItemStack getFilterItem() {
            return stack;
        }

        @Override
        public boolean stillValid(Player player) {
            return player.getMainHandItem().equals(stack);
        }

        @Override
        public boolean hasCustomBackDestination() {
            return false;
        }

        @Override
        public void goBack() {
        }
    }

    public record InventoryFilterAccess(ItemStack stack, IItemHandler itemHandler, int slot, Runnable goBackRunnable) implements FilterAccess {

        @Override
        public ItemStack getFilterItem() {
            return stack;
        }

        @Override
        public boolean stillValid(Player player) {
            // TODO: Maybe check the position of the container too so we can determine if its in range?
            //       Assumption is that we are though because we've been opened from another gui.
            return itemHandler.getStackInSlot(slot).equals(stack);
        }

        @Override
        public boolean hasCustomBackDestination() {
            return true;
        }

        @Override
        public void goBack() {
            if (hasCustomBackDestination()) {
                goBackRunnable.run();
            }
        }
    }
}
