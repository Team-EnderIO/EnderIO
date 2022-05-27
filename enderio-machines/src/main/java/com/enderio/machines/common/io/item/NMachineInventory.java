package com.enderio.machines.common.io.item;

import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.api.io.IIOConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.EnumMap;

/**
 * A machine inventory.
 * Configured and controlled by a machine's {@link IIOConfig} and a {@link NInventoryLayout}.
 */
public class NMachineInventory extends ItemStackHandler implements IEnderCapabilityProvider<IItemHandler> {
    private final IIOConfig config;
    private final NInventoryLayout layout;

    private final EnumMap<Direction, LazyOptional<Wrapped>> sideCache = new EnumMap<>(Direction.class);
    private LazyOptional<Wrapped> selfCache = LazyOptional.empty();

    /**
     * Create a new machine inventory.
     */
    public NMachineInventory(IIOConfig config, NInventoryLayout layout) {
        super(layout.getSlotCount());
        this.config = config;
        this.layout = layout;
    }

    /**
     * Get the IO config for the machine.
     */
    public final IIOConfig getConfig() {
        return config;
    }

    /**
     * Get the inventory layout.
     */
    public final NInventoryLayout getLayout() {
        return layout;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return layout.isItemValid(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return layout.getStackLimit(slot);
    }

    @Override
    public Capability<IItemHandler> getCapabilityType() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public LazyOptional<IItemHandler> getCapability(Direction side) {
        if (side == null) {
            // Create own cache if its been invalidated or not created yet.
            if (!selfCache.isPresent())
                selfCache = LazyOptional.of(() -> new Wrapped(this, null));
            return selfCache.cast();
        }

        if (!config.getMode(side).canConnect())
            return LazyOptional.empty();
        return sideCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new Wrapped(this, dir))).cast();
    }

    @Override
    public void invalidateSide(Direction side) {
        if (side != null) {
            if (sideCache.containsKey(side)) {
                sideCache.get(side).invalidate();
                sideCache.remove(side);
            }
        } else {
            selfCache.invalidate();
        }
    }

    @Override
    public void invalidateCaps() {
        for (LazyOptional<Wrapped> access : sideCache.values()) {
            access.invalidate();
        }
    }

    private record Wrapped(NMachineInventory master, @Nullable Direction side) implements IItemHandler {
        private Wrapped(NMachineInventory master, @Nullable Direction side) {
            this.master = master;
            this.side = side;
        }

        @Override
        public int getSlots() {
            return master.getSlots();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return master.getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            // Check we allow insertion on the slot
            if (!master.getLayout().canInsert(slot))
                return stack;

            // Check we allow input to the block on this side
            if (side != null && !master.getConfig().getMode(side).canInput())
                return stack;

            return master.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // Check we allow extraction on the slot
            if (!master.getLayout().canExtract(slot))
                return ItemStack.EMPTY;

            // Check we allow output from the block on this side
            if (side != null && !master.getConfig().getMode(side).canOutput())
                return ItemStack.EMPTY;

            return master.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return master.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return master.isItemValid(slot, stack);
        }
    }
}
