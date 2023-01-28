package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.ImpulseHopperMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ImpulseHopperBlockEntity extends PoweredMachineEntity {
    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable ENERGY_TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 16f);
    private static final int ENERGY_USAGE_PER_ITEM = 10;

    public ImpulseHopperBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_TRANSFER, ENERGY_USAGE, type, worldPosition, blockState);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ImpulseHopperMenu(this, inventory, containerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().inputSlot(6).outputSlot(6).ghostSlot(6).capacitor().build(); // first 6 input, second 6 output, third 6 ghost
    }

    @Override
    public void serverTick() {
        if (shouldActTick() && shouldPassItems()) {
            passItems();
        }
        super.serverTick();
    }

    public boolean shouldActTick() {// TODO General tick method for power consuming devices?
        return canAct() && level.getGameTime() % ticksForAction() == 0;
    }

    public int ticksForAction() {
        return 20;
    }

    public boolean canPass(int slot) {
        if (ItemStack.isSameItemSameTags(this.getInventory().getStackInSlot(slot), this.getInventory().getStackInSlot(slot + 6 + 6))) {
            return this.getInventory().getStackInSlot(slot).getCount() >= this.getInventory().getStackInSlot(slot + 6 + 6).getCount();
        }
        return false;
    }

    public boolean canHoldAndMerge(int slot) {
        // TODO: rewrite with slot access
        boolean canHold = this.getInventory().getStackInSlot(slot + 6).getCount() + this.getInventory().getStackInSlot(slot + 6 + 6).getCount() <= 64;
        boolean canMerge = ItemStack.isSameItemSameTags(this.getInventory().getStackInSlot(slot), this.getInventory().getStackInSlot(slot + 6));
        return canHold && canMerge;
    }

    public boolean shouldPassItems() {
        int totalpower = 0;
        for (int i = 0; i < 6; i++) {
            if (canPass(i) && canHoldAndMerge(i)) {
                totalpower += this.getInventory().getStackInSlot(i + 6 + 6).getCount() * ENERGY_USAGE_PER_ITEM;
                continue;
            }
            return false;
        }
        return this.getEnergyStorage().consumeEnergy(totalpower, true) > 0;
    }

    private void passItems() {
        for (int i = 0; i < 6; i++) {
            ItemStack stack = this.getInventory().getStackInSlot(i);
            ItemStack ghost = this.getInventory().getStackInSlot(i + 6 + 6);
            ItemStack result = this.getInventory().getStackInSlot(i + 6);
            if (ghost.isEmpty()) {
                continue;
            }
            if (result.isEmpty()) {
                result = stack.copy();
                result.setCount(ghost.getCount());
            } else if (stack.is(result.getItem())) {
                result.setCount(result.getCount() + ghost.getCount());
            }
            this.getEnergyStorage().consumeEnergy(ghost.getCount() * ENERGY_USAGE_PER_ITEM, false);
            stack.shrink(ghost.getCount());
            this.getInventory().setStackInSlot(i + 6, result);
        }
    }

    public boolean ghostSlotHasItem(int slot) {
        return !this.getInventory().getStackInSlot(slot + 6 + 6).isEmpty();
    }

}
