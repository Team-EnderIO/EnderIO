package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.energy.EnergyTransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerGeneratingMachineEntity extends PoweredMachineEntity {
    public PowerGeneratingMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(capacityKey, transferKey, consumptionKey, EnergyTransferMode.Extract, pType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (isServer()) {
            // If we're generating energy, add it to the buffer.
            if (isGenerating()) {
                energyStorage.addEnergy(getGenerationRate());
            }

            // Update block state
            if (getBlockState().getValue(ProgressMachineBlock.POWERED) != isGenerating()) {
                level.setBlock(getBlockPos(), getBlockState().setValue(ProgressMachineBlock.POWERED, isGenerating()), Block.UPDATE_ALL);
            }
        }

        super.tick();
    }

    public abstract boolean isGenerating();

    public abstract int getGenerationRate();
}
