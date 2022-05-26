package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// TODO: This should be replaced with creative power bank and buffer in the future, this is temporary :)
public class CreativePowerBlockEntity extends PoweredMachineEntity {
    public CreativePowerBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineCapacitorKeys.DEV_ENERGY_CAPACITY.get(),
            MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
            MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
            pType, pWorldPosition, pBlockState);
    }

    @Override
    public MachineTier getTier() {
        return MachineTier.Enhanced;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return null;
    }

    @Override
    public int getEnergyStored() {
        return getMaxEnergyStored() / 2;
    }

    @Override
    public boolean canInsertEnergy(@Nullable Direction side) {
        return false;
    }

    @Override
    public boolean canExtractEnergy(@Nullable Direction side) {
        return true;
    }
}
