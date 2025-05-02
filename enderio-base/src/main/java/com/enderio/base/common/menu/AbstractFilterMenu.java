package com.enderio.base.common.menu;

import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.IntSyncSlot;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// TODO: Test, but should provide everything we need to open the filters from the conduit UI.
public abstract class AbstractFilterMenu extends BaseEnderMenu {

    public static int BACK_BUTTON_ID = 0;

    @Nullable
    private final FilterAccess filterAccess;

    private final IntSyncSlot playerInventorySlot;

    /**
     * Server menu constructor
     */
    protected AbstractFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,
            FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory);
        this.filterAccess = filterAccess;

        this.playerInventorySlot = addSyncSlot(IntSyncSlot.readOnly(this::getPlayerInventorySlot));
    }

    /**
     * Client menu constructor.
     * Filter access should be created from the network buffer available in the client constructors.
     */
    protected AbstractFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.filterAccess = null;

        this.playerInventorySlot = addSyncSlot(IntSyncSlot.standalone());
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    protected ItemStack getFilterStack() {
        return Objects.requireNonNull(filterAccess).getFilterItem();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().isClientSide() || Objects.requireNonNull(filterAccess).stillValid(player);
    }

    @Override
    protected Slot createPlayerInventorySlot(Inventory inventory, int slot, int x, int y) {
        return new Slot(inventory, slot, x, y) {
            @Override
            public boolean mayPickup(Player player) {
                return slot != playerInventorySlot.get();
            }
        };
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

    @EnsureSide(EnsureSide.Side.SERVER)
    private int getPlayerInventorySlot() {
        if (filterAccess instanceof HandFilterAccess) {
            return getPlayerInventory().selected;
        }

        return -1;
    }

    public sealed interface FilterAccess {
        ItemStack getFilterItem();

        boolean stillValid(Player player);

        boolean hasCustomBackDestination();

        void goBack();
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

    public record InventoryFilterAccess(ItemStack stack, IItemHandler itemHandler, int slot, Runnable goBackRunnable)
            implements FilterAccess {

        @Override
        public ItemStack getFilterItem() {
            return stack;
        }

        @Override
        public boolean stillValid(Player player) {
            // TODO: Maybe check the position of the container too so we can determine if
            // its in range?
            // Assumption is that we are though because we've been opened from another gui.
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
