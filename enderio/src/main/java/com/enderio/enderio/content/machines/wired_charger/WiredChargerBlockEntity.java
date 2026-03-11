package com.enderio.enderio.content.machines.wired_charger;

import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class WiredChargerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_USAGE);

    public static final SingleSlotAccess ITEM_TO_CHARGE = new SingleSlotAccess();
    public static final SingleSlotAccess ITEM_CHARGED = new SingleSlotAccess();

    private float progress = 0;

    public WiredChargerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.WIRED_CHARGER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .capacitor()
                .inputSlot((slot, stack) -> acceptItem(stack))
                .slotAccess(ITEM_TO_CHARGE)
                .outputSlot()
                .slotAccess(ITEM_CHARGED)
                .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new WiredChargerMenu(containerId, playerInventory, this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (isActive()) {
            chargeItem();
        } else {
            this.progress = 0;
        }
    }

    @Override
    public boolean isActive() {
        return hasEnergy() && canAct();
    }

    public boolean acceptItem(ItemStack item) {
        return item.getCapability(ForgeCapabilities.ENERGY) != null;
    }

    @Override
    public boolean canAct() {
        ItemStack inputItem = ITEM_TO_CHARGE.getItemStack(this);
        ItemStack outputItem = ITEM_CHARGED.getItemStack(this);
        return !inputItem.isEmpty() && outputItem.isEmpty() && acceptItem(inputItem) && super.canAct();
    }

    public void chargeItem() {
        ItemStack chargeable = ITEM_TO_CHARGE.getItemStack(this);
        IEnergyStorage itemEnergyHandler = chargeable.getCapability(ForgeCapabilities.ENERGY);

        if (itemEnergyHandler != null) {
            if (itemEnergyHandler.getEnergyStored() == itemEnergyHandler.getMaxEnergyStored()) {
                ITEM_CHARGED.setStackInSlot(this, chargeable);
                ITEM_TO_CHARGE.setStackInSlot(this, ItemStack.EMPTY);
            } else {
                int energyToInsert = Math.min(
                        itemEnergyHandler.getMaxEnergyStored() - itemEnergyHandler.getEnergyStored(),
                        Math.max(getEnergyStorage().getEnergyStored(), getEnergyStorage().getMaxEnergyUse()));

                if (energyToInsert > 0) {
                    int taken = getEnergyStorage().consumeEnergy(energyToInsert, true);
                    int accepted = itemEnergyHandler.receiveEnergy(taken, true);
                    itemEnergyHandler.receiveEnergy(accepted, false);
                    getEnergyStorage().consumeEnergy(accepted, false);
                    this.progress = (float) itemEnergyHandler.getEnergyStored()
                            / itemEnergyHandler.getMaxEnergyStored();
                }
            }
        }
    }

    public float getChargeProgress() {
        return this.progress;
    }
}
