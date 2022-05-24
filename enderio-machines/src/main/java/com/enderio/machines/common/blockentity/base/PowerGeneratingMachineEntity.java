package com.enderio.machines.common.blockentity.base;

import com.enderio.machines.common.energy.EnergyTransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @deprecated This is probably not terribly useful, so I'm gonna remove it once the Stirling Generator is implemented fully. Right now its just a nice to have.
 */
@Deprecated(forRemoval = true)
public abstract class PowerGeneratingMachineEntity extends PoweredMachineEntity {
    public PowerGeneratingMachineEntity(EnergyTransferMode transferMode, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(transferMode, pType, pWorldPosition, pBlockState);
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
