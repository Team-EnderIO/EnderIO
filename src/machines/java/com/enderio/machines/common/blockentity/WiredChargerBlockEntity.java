package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.sync.FloatDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.WiredChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WiredChargerBlockEntity extends PoweredMachineEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 100000f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 100f);

    public static final SingleSlotAccess ITEM_TO_CHARGE = new SingleSlotAccess();

    public static final SingleSlotAccess ITEM_CHARGED = new SingleSlotAccess();

    private float progress = 0;

    public WiredChargerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);
        addDataSlot(new FloatDataSlot(this::getProgress, p -> progress = p, SyncMode.GUI));
    }


    @Override
    public MachineInventoryLayout getInventoryLayout() {
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
        chargeItem();
    }


    public boolean acceptItem(ItemStack item) {
        Optional<IEnergyStorage> energyHandlerCap = item.getCapability(ForgeCapabilities.ENERGY).resolve();
        return energyHandlerCap.isPresent();
    }

    public void chargeItem() {
        ItemStack inputItem = ITEM_TO_CHARGE.getItemStack(this);
        ItemStack outputItem = ITEM_CHARGED.getItemStack(this);
        if (!inputItem.isEmpty() && outputItem.isEmpty() && isCapacitorInstalled()) {
            Optional<IEnergyStorage> energyHandlerCap = inputItem.getCapability(ForgeCapabilities.ENERGY).resolve();
            if (energyHandlerCap.isPresent()) {
                IEnergyStorage itemEnergyStorage = energyHandlerCap.get();
                if (itemEnergyStorage.getEnergyStored() == itemEnergyStorage.getMaxEnergyStored()) {
                    ITEM_CHARGED.setStackInSlot(this, inputItem);
                    ITEM_TO_CHARGE.setStackInSlot(this, ItemStack.EMPTY);
                } else {
                    // The energyExtracted per tick should increase if the charged item has more energy and with the tiers of the capacitor installed
                    int energyExtracted = itemEnergyStorage.getMaxEnergyStored() / (1000-this.energyStorage.getMaxEnergyUse());
                    if (canAct() && this.energyStorage.getEnergyStored() >= energyExtracted) {
                        itemEnergyStorage.receiveEnergy(energyExtracted, false);
                        this.energyStorage.takeEnergy(energyExtracted);
                        this.progress = (float)itemEnergyStorage.getEnergyStored()/itemEnergyStorage.getMaxEnergyStored();
                    }
                }
            } else {
                this.progress = 0;
            }
        } else {
            this.progress = 0;
        }
    }

    public float getProgress() {
        return this.progress;
    }
}
