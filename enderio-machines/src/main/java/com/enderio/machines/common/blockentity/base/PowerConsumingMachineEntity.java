package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.energy.EnergyIOMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerConsumingMachineEntity extends PoweredMachineEntity {
    public PowerConsumingMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, capacityKey, transferKey, consumptionKey, type, worldPosition, blockState);
    }
}
