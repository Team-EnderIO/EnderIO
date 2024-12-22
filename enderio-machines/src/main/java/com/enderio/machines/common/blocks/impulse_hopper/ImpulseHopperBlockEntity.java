package com.enderio.machines.common.blocks.impulse_hopper;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ImpulseHopperBlockEntity extends PoweredMachineBlockEntity {
    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.IMPULSE_HOPPER_CAPACITY);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.IMPULSE_HOPPER_USAGE);
    private static final int ENERGY_USAGE_PER_ITEM = 10; // TODO: What is? surely should use the ENERGY_USAGE key

    public static final MultiSlotAccess INPUT = new MultiSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();
    public static final MultiSlotAccess GHOST = new MultiSlotAccess();

    public ImpulseHopperBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.IMPULSE_HOPPER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ImpulseHopperMenu(containerId, inventory, this);
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot(6,
                        (integer, itemStack) -> ItemStack.isSameItemSameComponents(itemStack,
                                GHOST.get(integer).getItemStack(this)))
                .slotAccess(INPUT)
                .outputSlot(6)
                .slotAccess(OUTPUT)
                .ghostSlot(6)
                .slotAccess(GHOST)
                .capacitor()
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
        ItemStack input = INPUT.get(slot).getItemStack(this);
        ItemStack ghost = GHOST.get(slot).getItemStack(this);
        if (ItemStack.isSameItemSameComponents(input, ghost)) {
            return input.getCount() >= ghost.getCount();
        }
        return false;
    }

    public boolean canHoldAndMerge(int slot) {
        boolean canHold = OUTPUT.get(slot).getItemStack(this).getCount()
                + GHOST.get(slot).getItemStack(this).getCount() <= GHOST.get(slot).getItemStack(this).getMaxStackSize();
        boolean canMerge = ItemStack.isSameItemSameComponents(INPUT.get(slot).getItemStack(this),
                GHOST.get(slot).getItemStack(this));
        return canHold && canMerge;
    }

    public boolean shouldPassItems() {
        int totalpower = 0;
        for (int i = 0; i < 6; i++) {
            if (canPass(i) && canHoldAndMerge(i)) {
                totalpower += GHOST.get(i).getItemStack(this).getCount() * ENERGY_USAGE_PER_ITEM;
                continue;
            }
            return false;
        }
        return this.getEnergyStorage().consumeEnergy(totalpower, true) > 0;
    }

    private void passItems() {
        for (int i = 0; i < 6; i++) {
            ItemStack stack = INPUT.get(i).getItemStack(this);
            ItemStack ghost = GHOST.get(i).getItemStack(this);
            ItemStack result = OUTPUT.get(i).getItemStack(this);
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
            OUTPUT.get(i).setStackInSlot(this, result);
        }
    }

    public boolean ghostSlotHasItem(int slot) {
        return GHOST.get(slot).getItemStack(this).isEmpty();
    }
}
