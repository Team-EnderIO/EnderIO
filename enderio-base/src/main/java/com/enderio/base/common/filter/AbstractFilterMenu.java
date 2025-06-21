package com.enderio.base.common.filter;

import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.IntSyncSlot;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFilterMenu<T> extends BaseEnderMenu {

    public static final int BACK_BUTTON_ID = 0;

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

    protected abstract Supplier<DataComponentType<T>> dataComponentType();
    protected abstract T defaultFilter();

    @EnsureSide(EnsureSide.Side.SERVER)
    protected T getFilter() {
        return getFilterStack().getOrDefault(dataComponentType(), defaultFilter());
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    protected void setFilter(T filter) {
        var stack = getFilterStack().copy();
        stack.set(dataComponentType(), filter);
        setFilterStack(stack);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    protected void modifyFilter(Function<T, T> modifier) {
        setFilter(modifier.apply(getFilter()));
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    protected ItemStack getFilterStack() {
        return Objects.requireNonNull(filterAccess).getFilterItem();
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    protected void setFilterStack(ItemStack stack) {
        Objects.requireNonNull(filterAccess).setFilterItem(stack);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    protected void modifyFilterStack(Function<ItemStack, ItemStack> modifier) {
        setFilterStack(modifier.apply(getFilterStack()));
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
            if (filterAccess == null || !filterAccess.goBack()) {
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

    @Override
    public void doClick(int slotId, int button, ClickType clickType, Player player) {
        if (slotId > 0 && slotId < slots.size() && getSlot(slotId) instanceof FilterSlot<?> filterSlot) {
            // Only allow PICKUP (click) or QUICK_MOVE (shift + click) events.
            if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) {
                return;
            }

            if (!filterSlot.isEmpty()) {
                filterSlot.clearResource();
                return;
            }
        }

        super.doClick(slotId, button, clickType, player);
    }

    public sealed interface FilterAccess {
        ItemStack getFilterItem();

        void setFilterItem(ItemStack stack);

        boolean stillValid(Player player);

        boolean goBack();
    }

    public static final class HandFilterAccess implements FilterAccess {
        private final Player player;
        private ItemStack stack;

        public HandFilterAccess(Player player, ItemStack stack) {
            this.player = player;
            this.stack = stack;
        }

        @Override
        public ItemStack getFilterItem() {
            return stack.copy();
        }

        @Override
        public void setFilterItem(ItemStack stack) {
            player.setItemSlot(EquipmentSlot.MAINHAND, stack);
            this.stack = stack;
        }

        @Override
        public boolean stillValid(Player player) {
            return player.getMainHandItem().equals(stack);
        }

        @Override
        public boolean goBack() {
            return false;
        }
    }

    public static final class InventoryFilterAccess implements FilterAccess {
        private ItemStack stack;
        private final IItemHandlerModifiable itemHandler;
        private final int slot;
        private final @Nullable Runnable goBackRunnable;

        public InventoryFilterAccess(ItemStack stack, IItemHandlerModifiable itemHandler, int slot,
                @Nullable Runnable goBackRunnable) {
            this.stack = stack;
            this.itemHandler = itemHandler;
            this.slot = slot;
            this.goBackRunnable = goBackRunnable;
        }

        @Override
        public ItemStack getFilterItem() {
            return stack.copy();
        }

        @Override
        public void setFilterItem(ItemStack stack) {
            // Mainly just to deal with handlers that either return a copy or need to
            // setChanged.
            itemHandler.setStackInSlot(slot, stack);
            this.stack = stack;
        }

        @Override
        public boolean stillValid(Player player) {
            return itemHandler.getStackInSlot(slot).equals(stack);
        }

        @Override
        public boolean goBack() {
            if (goBackRunnable != null) {
                goBackRunnable.run();
                return true;
            } else {
                return false;
            }
        }
    }
}
