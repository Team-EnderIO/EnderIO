package com.enderio.enderio.content.machines.impulse_hopper;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class ImpulseHopperBlockEntity extends PoweredMachineBlockEntity {
    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.IMPULSE_HOPPER_CAPACITY);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.IMPULSE_HOPPER_USAGE);
    private static final int ENERGY_USAGE_PER_ITEM = 10; // TODO: What is? surely should use the ENERGY_USAGE key

    public static final MultiResourceSlotKey<ItemResource> INPUT = new MultiResourceSlotKey<>(6);
    public static final MultiResourceSlotKey<ItemResource> OUTPUT = new MultiResourceSlotKey<>(6);
    public static final MultiResourceSlotKey<ItemResource> GHOST = new MultiResourceSlotKey<>(6);
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    public ImpulseHopperBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.IMPULSE_HOPPER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ImpulseHopperMenu(containerId, inventory, this);
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(INPUT, SlotTemplates.input(), b -> b
                // Note this filter is a bit rubbish - checking by ID only works because we're slot 0-5. Review sometime.
                .filter((index, itemResource) -> itemResource.matches(getInventory().getStack(GHOST.slot(index)))))
            .add(OUTPUT, SlotTemplates.output())
            .add(GHOST, SlotTemplates.ghost())
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    @Override
    public void serverTick() {
        if (shouldActTick() && shouldPassItems()) {
            passItems();
        }
        super.serverTick();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy();
    }

    public boolean shouldActTick() {// TODO General tick method for power consuming devices?
        return canAct() && level.getGameTime() % ticksForAction() == 0;
    }

    public int ticksForAction() {
        return 20;
    }

    public boolean canPass(int slot) {
        ItemStack input = getInventory().getStack(INPUT.slot(slot));
        ItemStack ghost = getInventory().getStack(GHOST.slot(slot));
        if (ItemStack.isSameItemSameComponents(input, ghost)) {
            return input.getCount() >= ghost.getCount();
        }
        return false;
    }

    public boolean canHoldAndMerge(int slot) {
        boolean canHold = getInventory().getStack(OUTPUT.slot(slot)).getCount()
                + getInventory().getStack(GHOST.slot(slot)).getCount() <= getInventory().getStack(GHOST.slot(slot)).getMaxStackSize();
        boolean canMerge = ItemStack.isSameItemSameComponents(getInventory().getStack(INPUT.slot(slot)),
                getInventory().getStack(GHOST.slot(slot)));
        return canHold && canMerge;
    }

    public boolean shouldPassItems() {
        int totalpower = 0;
        for (int i = 0; i < 6; i++) {
            if (canPass(i) && canHoldAndMerge(i)) {
                totalpower += getInventory().getStack(GHOST.slot(i)).getCount() * ENERGY_USAGE_PER_ITEM;
                continue;
            }
            return false;
        }
        return this.getEnergyStorage().canConsumeAtLeast(totalpower);
    }

    private void passItems() {
        for (int i = 0; i < 6; i++) {
            ItemStack stack = getInventory().getStack(INPUT.slot(i));
            ItemStack ghost = getInventory().getStack(GHOST.slot(i));
            ItemStack result = getInventory().getStack(OUTPUT.slot(i));
            if (ghost.isEmpty()) {
                continue;
            }
            if (result.isEmpty()) {
                result = stack.copy();
                result.setCount(ghost.getCount());
            } else if (stack.is(result.getItem())) {
                result.setCount(result.getCount() + ghost.getCount());
            }
            this.getEnergyStorage().consume(ghost.getCount() * ENERGY_USAGE_PER_ITEM, null);
            stack.shrink(ghost.getCount());
            getInventory().setStack(OUTPUT.slot(i), result);
        }
    }

    public boolean ghostSlotHasItem(int slot) {
        return getInventory().getStack(GHOST.slot(slot)).isEmpty();
    }
}
