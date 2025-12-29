package com.enderio.enderio.foundation.menu.legacy;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.block.entity.legacy.LegacyPoweredMachineBlockEntity;
import com.enderio.enderio.foundation.energy.EnergyStorageInfo;
import com.enderio.enderio.foundation.io.energy.IMachineEnergyStorage;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

@Deprecated(forRemoval = true, since = "7.1")
public class LegacyPoweredMachineMenu<T extends LegacyPoweredMachineBlockEntity> extends LegacyMachineMenu<T> {
    protected static final Identifier EMPTY_CAPACITOR_SLOT = EnderIO.rl("item/empty_capacitor_slot");

    protected LegacyPoweredMachineMenu(@Nullable MenuType<?> menuType, int containerId, @Nullable T blockEntity,
            Inventory playerInventory) {
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

    public EnergyStorageInfo getEnergyStorage() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return new EnergyStorageInfo(getBlockEntity().getEnergyStorage().getEnergyStored(), getBlockEntity().getEnergyStorage().getMaxEnergyStored());
    }
}
