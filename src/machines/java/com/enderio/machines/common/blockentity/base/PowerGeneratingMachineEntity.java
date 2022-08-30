package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.ICapacitorScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.block.ProgressMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// TODO: Remaster power generation sometime.
public abstract class PowerGeneratingMachineEntity extends PoweredMachineEntity {
    public PowerGeneratingMachineEntity(ICapacitorScalable capacityKey, ICapacitorScalable transferKey, ICapacitorScalable consumptionKey, BlockEntityType<?> type, BlockPos worldPosition,
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
