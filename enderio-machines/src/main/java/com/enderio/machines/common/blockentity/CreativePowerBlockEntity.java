package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.energy.EnergyIOMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// TODO: This should be replaced with creative power bank and buffer in the future, this is temporary :)
public class CreativePowerBlockEntity extends PoweredMachineEntity {
    public CreativePowerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Output,
            MachineCapacitorKeys.DEV_ENERGY_CAPACITY.get(),
            MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
            MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
            type, worldPosition, blockState);
    }

    @Override
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey useKey) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, this::getCapacitorData, capacityKey, transferKey, useKey) {
            @Override
            public int getEnergyStored() {
                return getMaxEnergyStored() / 2;
            }
        };
    }

    @Override
    public MachineTier getTier() {
        return MachineTier.ENHANCED;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return null;
    }
}
