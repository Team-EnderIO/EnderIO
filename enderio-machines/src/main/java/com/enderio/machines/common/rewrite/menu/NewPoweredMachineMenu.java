package com.enderio.machines.common.rewrite.menu;

import com.enderio.EnderIOBase;
import com.enderio.machines.common.network.menu.EnergyStorageSyncSlot;
import com.enderio.machines.common.rewrite.blockentity.NewPoweredMachineBlockEntity;
import com.enderio.machines.common.rewrite.energy.EnergyStorageInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class NewPoweredMachineMenu<T extends NewPoweredMachineBlockEntity> extends NewMachineMenu<T> {
    protected static final ResourceLocation EMPTY_CAPACITOR_SLOT = EnderIOBase.loc("item/empty_capacitor_slot");

    private final EnergyStorageSyncSlot energySyncSlot;

    protected NewPoweredMachineMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,
            T blockEntity) {
        super(menuType, containerId, playerInventory, blockEntity);

        energySyncSlot = addSyncSlot(
                EnergyStorageSyncSlot.readOnly(() -> EnergyStorageInfo.of(blockEntity.getEnergyStorage())));
    }

    protected NewPoweredMachineMenu(@Nullable MenuType<?> menuType, BlockEntityType<? extends T> blockEntityType,
            int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(menuType, blockEntityType, containerId, playerInventory, buf);

        energySyncSlot = addSyncSlot(EnergyStorageSyncSlot.standalone());
    }

    public EnergyStorageInfo getEnergyStorage() {
        return energySyncSlot.get();
    }

    public boolean isCapacitorInstalled() {
        return getBlockEntity().isCapacitorInstalled();
    }

    public int getCapacitorSlotIndex() {
        return getBlockEntity().getCapacitorSlotIndex();
    }
}
