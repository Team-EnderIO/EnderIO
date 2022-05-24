package com.enderio.machines.common.blockentity.base;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.energy.EnergyTransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerGeneratingMachineEntity extends PoweredMachineEntity {
    public PowerGeneratingMachineEntity(MachineTier tier, EnergyTransferMode transferMode, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(tier, transferMode, pType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (isGenerating()) {
            energyStorage.addEnergy(getGenerationRate());
        }

        super.tick();
    }

    public abstract boolean isGenerating();

    public abstract int getGenerationRate();
}
