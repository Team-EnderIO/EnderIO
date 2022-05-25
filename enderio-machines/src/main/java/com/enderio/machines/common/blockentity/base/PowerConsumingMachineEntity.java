package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.machines.common.blockentity.data.sidecontrol.IOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerConsumingMachineEntity extends PoweredMachineEntity {
    public PowerConsumingMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, consumptionKey, pType, pWorldPosition, pBlockState);
    }

    /**
     * @deprecated Kind of useless.
     */
    @Deprecated
    public boolean hasEnergy() {
        return getEnergyStored() > 0;
    }

    @Override
    public boolean canInsertEnergy(Direction side) {
        return getIoConfig().getIO(side) != IOConfig.IOState.DISABLED;
    }

    @Override
    public boolean canExtractEnergy(Direction side) {
        return false;
    }
}
