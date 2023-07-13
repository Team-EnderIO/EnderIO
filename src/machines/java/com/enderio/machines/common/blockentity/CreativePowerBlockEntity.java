package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.FixedScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
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
public class CreativePowerBlockEntity extends PoweredMachineBlockEntity {

    public static final FixedScalable CAPACITY = new FixedScalable(() -> Integer.MAX_VALUE);

    public CreativePowerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Output, CAPACITY, FixedScalable.ZERO, type, worldPosition, blockState);
    }

    @Override
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacityKey,
        Supplier<Integer> useKey) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, capacityKey, useKey) {
            @Override
            public int getEnergyStored() {
                return getMaxEnergyStored() / 2;
            }
        };
    }

    @Override
    protected boolean isActive() {
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return null;
    }
}
