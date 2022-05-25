package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PowerGeneratingMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.energy.EnergyTransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class StirlingGeneratorBlockEntity extends PowerGeneratingMachineEntity {
    public StirlingGeneratorBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(capacityKey, transferKey, consumptionKey, pType, pWorldPosition, pBlockState);
    }

    @Override
    public Optional<ItemSlotLayout> getSlotLayout() {
        if (getTier() == MachineTier.Simple) {
            return Optional.of(ItemSlotLayout.basic(1, 0));
        }
        return Optional.of(ItemSlotLayout.withCapacitor(1, 0));
    }

    @Override
    public void tick() {
        // TODO: Fuel burning system.

        super.tick();
    }

    @Override
    public boolean isGenerating() {
        return true;
    }

    @Override
    public int getGenerationRate() {
        return 1;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return null;
    }
}
