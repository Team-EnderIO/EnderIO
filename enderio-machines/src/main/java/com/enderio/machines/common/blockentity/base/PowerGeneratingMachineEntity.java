package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.machines.common.energy.EnergyTransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerGeneratingMachineEntity extends PoweredMachineEntity {
    public PowerGeneratingMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(capacityKey, transferKey, consumptionKey, EnergyTransferMode.Extract, pType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        // If we're generating energy, add it to the buffer.
        if (isGenerating()) {
            energyStorage.addEnergy(getGenerationRate());
        }

        super.tick();
    }

    public abstract boolean isGenerating();

    public abstract int getGenerationRate();
}
