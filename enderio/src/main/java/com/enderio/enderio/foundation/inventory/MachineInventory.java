package com.enderio.enderio.foundation.inventory;

import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.foundation.state.MachineState;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * A machine inventory.
 * Configured and controlled by a machine's {@link IOConfigurable} and a {@link MachineInventoryLayout}.
 */
public class MachineInventory extends ItemStacksResourceHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final IOConfigurable ioConfigurable;
    private final MachineInventoryLayout layout;

    /**
     * Create a new machine inventory.
     */
    public MachineInventory(IOConfigurable ioConfigurable, MachineInventoryLayout layout) {
        super(layout.getSlotCount());
        this.ioConfigurable = ioConfigurable;
        this.layout = layout;
    }

    public final MachineInventoryLayout layout() {
        return layout;
    }

    public ItemStack getStack(int index) {
        return getResource(index).toStack(getAmountAsInt(index));
    }

    public void setStack(int index, ItemStack stack) {
        set(index, ItemResource.of(stack), stack.getCount());
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return layout.isItemValid(index, resource);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return layout.getStackLimit(index);
    }

    @Nullable
    public ResourceHandler<ItemResource> getForSide(@Nullable Direction side) {
        if (side == null) {
            return new Wrapped(this, null);
        }

        if (ioConfigurable.getIOMode(side).canConnect()) {
            return new Wrapped(this, side);
        }

        return null;
    }

    public void copyFromItem(ItemContainerContents contents) {
        contents.copyInto(this.stacks);
    }

    public ItemContainerContents toItemContents() {
        return ItemContainerContents.fromItems(this.stacks);
    }

    // TODO: not a fan of this pattern.
    public void updateMachineState(MachineState state, boolean add) {

    }

    @Override
    public void deserialize(ValueInput input) {
        input.listOrEmpty("Items", ItemStackWithSlot.CODEC).forEach(slot -> {
            if (slot.isValidInContainer(layout().getSlotCount())) {
                stacks.set(slot.slot(), slot.stack());
            } else {
                LOGGER.warn("Skipping item from slot {}, as it is outside the bounds of the inventory.", slot);
            }
        });
    }

    @Override
    public void serialize(ValueOutput output) {
        ValueOutput.TypedOutputList<ItemStackWithSlot> itemList = output.list("Items", ItemStackWithSlot.CODEC);
        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i);
            if (!stack.isEmpty()) {
                itemList.add(new ItemStackWithSlot(i, stack));
            }
        }
    }

    private record Wrapped(MachineInventory machineInventory, @Nullable Direction side) implements ResourceHandler<ItemResource> {

        @Override
        public int getAmountAsInt(int index) {
            return machineInventory.getAmountAsInt(index);
        }

        @Override
        public int getCapacityAsInt(int index, ItemResource resource) {
            return machineInventory.getCapacityAsInt(index, resource);
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            return machineInventory.insert(resource, amount, transaction);
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return machineInventory.extract(resource, amount, transaction);
        }

        @Override
        public int size() {
            return machineInventory.size();
        }

        @Override
        public ItemResource getResource(int index) {
            return machineInventory.getResource(index);
        }

        @Override
        public long getAmountAsLong(int index) {
            return machineInventory.getAmountAsLong(index);
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return machineInventory.getCapacityAsLong(index, resource);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return machineInventory.isValid(index, resource);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            // Check we allow insertion on the slot
            if (!machineInventory.layout().canInsert(index)) {
                return 0;
            }

            // Check we allow input to the block on this side
            if (side != null && !machineInventory.ioConfigurable.getIOMode(side).canInput()) {
                return 0;
            }

            return machineInventory.insert(index, resource, amount, transaction);
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            // Check we allow extraction on the slot
            if (!machineInventory.layout().canExtract(index)) {
                return 0;
            }

            // Check we allow output from the block on this side
            if (side != null && !machineInventory.ioConfigurable.getIOMode(side).canOutput()) {
                return 0;
            }

            return machineInventory.extract(index, resource, amount, transaction);
        }
    }
}
