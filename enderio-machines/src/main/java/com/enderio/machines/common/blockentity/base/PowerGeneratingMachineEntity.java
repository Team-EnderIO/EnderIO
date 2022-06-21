package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.energy.EnergyIOMode;
import com.enderio.machines.common.block.ProgressMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// TODO: Remaster this sometime to work the same as 1.12
public abstract class PowerGeneratingMachineEntity extends PoweredMachineEntity {
    public PowerGeneratingMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> type, BlockPos worldPosition,
        BlockState blockState) {
        super(EnergyIOMode.Output, capacityKey, transferKey, consumptionKey, type, worldPosition, blockState);
    }

    @Override
    public void serverTick() {
        // If we're generating energy, add it to the buffer.
        if (isGenerating()) {
            getEnergyStorage().addEnergy(getGenerationRate());
        }

        // Update block state
        if (getBlockState().getValue(ProgressMachineBlock.POWERED) != isGenerating()) {
            level.setBlock(getBlockPos(), getBlockState().setValue(ProgressMachineBlock.POWERED, isGenerating()), Block.UPDATE_ALL);
        }

        super.serverTick();
    }

    public abstract boolean isGenerating();

    public abstract int getGenerationRate();
}
