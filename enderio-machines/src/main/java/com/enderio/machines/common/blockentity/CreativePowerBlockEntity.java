package com.enderio.machines.common.blockentity;

import com.enderio.api.capability.ICapacitorData;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.energy.EnergyTransferMode;
import com.enderio.machines.common.energy.MachineEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CreativePowerBlockEntity extends PoweredMachineEntity {
    public CreativePowerBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineTier.ENHANCED, EnergyTransferMode.Extract, pType, pWorldPosition, pBlockState);
    }

    @Override
    protected MachineEnergyStorage createEnergyStorage(EnergyTransferMode transferMode) {
        return new MachineEnergyStorage(() -> Optional.of(CapacitorUtil.OCTADIC), transferMode) {
            @Override
            public int getEnergyStored() {
                return getMaxEnergyStored();
            }

            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return null;
    }
}
