package com.enderio.machines.common.blockentity;

import com.enderio.base.api.capacitor.FixedScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.LegacyPoweredMachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// TODO: This should be replaced with creative power bank and buffer in the future, this is temporary :)
public class CreativePowerBlockEntity extends LegacyPoweredMachineBlockEntity {

    public static final FixedScalable CAPACITY = new FixedScalable(() -> Integer.MAX_VALUE);

    public CreativePowerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Output, CAPACITY, FixedScalable.ZERO, MachineBlockEntities.CREATIVE_POWER.get(),
                worldPosition, blockState);
    }

    @Override
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacityKey,
            Supplier<Integer> useKey) {
        return new MachineEnergyStorage(this, energyIOMode, capacityKey, useKey) {
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
