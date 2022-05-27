package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.energy.EnergyIOMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerConsumingMachineEntity extends PoweredMachineEntity {
    public PowerConsumingMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(EnergyIOMode.Input, capacityKey, transferKey, consumptionKey, pType, pWorldPosition, pBlockState);
    }

    // TODO: Power consumption helpers? is that a good idea?

    /**
     * Try to consume energy.
     * Capped at the machines max use rate.
     */
    public final int consumeEnergy(int maxConsume) {
        return getEnergyStorage().consumeEnergy(Math.min(maxConsume, getMaxEnergyUse()));
    }
}
