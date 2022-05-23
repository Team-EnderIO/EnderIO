package com.enderio.machines.common.blockentity.base;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.energy.EnergyTransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerConsumingMachineEntity extends PoweredMachineEntity {
    public PowerConsumingMachineEntity(MachineTier tier, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(tier, EnergyTransferMode.Insert, pType, pWorldPosition, pBlockState);
    }

    public boolean hasEnergy() {
        return energyStorage.getEnergyStored() > 0;
    }

    public boolean canConsumeEnergy(int energy) {
        return energyStorage.hasEnergy(energy);
    }

    public int consumeEnergy(int energy) {
        return energyStorage.consumeEnergy(energy);
    }
}
