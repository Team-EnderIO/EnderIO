package com.enderio.machines.common.blockentity;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.WiredChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class WiredChargerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_USAGE);

    public static final SingleSlotAccess ITEM_TO_CHARGE = new SingleSlotAccess();
    public static final SingleSlotAccess ITEM_CHARGED = new SingleSlotAccess();

    private float progress = 0;

    public WiredChargerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, MachineBlockEntities.WIRED_CHARGER.get(), worldPosition, blockState);
        addDataSlot(NetworkDataSlot.FLOAT.create(this::getProgress, p -> progress = p));
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout
            .builder()
            .capacitor()
            .inputSlot((slot, stack) -> acceptItem(stack))
            .slotAccess(ITEM_TO_CHARGE)
            .outputSlot()
            .slotAccess(ITEM_CHARGED)
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WiredChargerMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (canAct()) {
            chargeItem();
        } else {
            this.progress = 0;
        }
    }

    @Override
    protected boolean isActive() {
        return canAct();
    }

    public boolean acceptItem(ItemStack item) {
        return item.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }
	
	@Override
    public boolean canAct() {
        ItemStack inputItem = ITEM_TO_CHARGE.getItemStack(this);
        ItemStack outputItem = ITEM_CHARGED.getItemStack(this);
        return !inputItem.isEmpty() && outputItem.isEmpty() && acceptItem(inputItem) && super.canAct();
    }

    public void chargeItem() {
        ItemStack chargeable = ITEM_TO_CHARGE.getItemStack(this);
        IEnergyStorage itemEnergyHandler = chargeable.getCapability(Capabilities.EnergyStorage.ITEM);

        if (itemEnergyHandler != null) {
            if (itemEnergyHandler.getEnergyStored() == itemEnergyHandler.getMaxEnergyStored()) {
                ITEM_CHARGED.setStackInSlot(this, chargeable);
                ITEM_TO_CHARGE.setStackInSlot(this, ItemStack.EMPTY);
            } else {
                //todo energy balancing
                // The energyExtracted per tick should increase if the charged item has more energy and with the tier of the capacitor installed
                int energyExtracted = itemEnergyHandler.getMaxEnergyStored() / ((int)getCapacitorData().getModifier(CapacitorModifier.ENERGY_USE)*333 -this.energyStorage.getMaxEnergyUse());

                if (this.energyStorage.getEnergyStored() >= energyExtracted) {
                    itemEnergyHandler.receiveEnergy(energyExtracted, false);
                    this.energyStorage.takeEnergy(energyExtracted);
                    this.progress = (float)itemEnergyHandler.getEnergyStored() / itemEnergyHandler.getMaxEnergyStored();
                }
            }
        }
    }

    public float getProgress() {
        return this.progress;
    }
}
