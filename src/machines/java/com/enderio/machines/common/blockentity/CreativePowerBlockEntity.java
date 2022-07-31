package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.ScalableValue;
import com.enderio.api.capacitor.Scalers;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// TODO: This should be replaced with creative power bank and buffer in the future, this is temporary :)
public class CreativePowerBlockEntity extends PoweredMachineEntity {

    // Daft dummy values
    public static final ScalableValue CAPACITY = new ScalableValue(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f, Scalers.ENERGY);
    public static final ScalableValue TRANSFER = new ScalableValue(CapacitorModifier.ENERGY_TRANSFER, () -> 120f, Scalers.ENERGY);
    public static final ScalableValue USAGE = new ScalableValue(CapacitorModifier.ENERGY_USE, () -> 30f, Scalers.ENERGY);

    public CreativePowerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Output, CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);
    }

    @Override
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacityKey, Supplier<Integer> transferKey,
        Supplier<Integer> useKey) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, capacityKey, transferKey, useKey) {
            @Override
            public int getEnergyStored() {
                return getMaxEnergyStored() / 2;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return null;
    }
}
