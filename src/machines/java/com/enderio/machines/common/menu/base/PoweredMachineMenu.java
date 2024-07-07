package com.enderio.machines.common.menu.base;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class PoweredMachineMenu<T extends PoweredMachineBlockEntity> extends MachineMenu<T> {
    protected static final ResourceLocation EMPTY_CAPACITOR_SLOT = EnderIO.loc("item/empty_capacitor_slot");

    protected PoweredMachineMenu(@Nullable MenuType<?> menuType, int containerId, @Nullable T blockEntity, Inventory playerInventory) {
        super(menuType, containerId, blockEntity, playerInventory);
    }

    public boolean isCapacitorInstalled() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().isCapacitorInstalled();
    }

    public int getCapacitorSlotIndex() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getCapacitorSlot();
    }

    public IMachineEnergyStorage getEnergyStorage() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getEnergyStorage();
    }
}
