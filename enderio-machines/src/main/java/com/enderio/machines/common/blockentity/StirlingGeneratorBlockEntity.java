package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PowerGeneratingMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.energy.EnergyTransferMode;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class StirlingGeneratorBlockEntity extends PowerGeneratingMachineEntity {
    // TODO: Add capacitor keys.
    public static class Simple extends StirlingGeneratorBlockEntity {
        public Simple(BlockEntityType<?> pType, BlockPos pWorldPosition,
            BlockState pBlockState) {
            super(MachineCapacitorKeys.DEV_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Simple;
        }
    }

    public static class Standard extends StirlingGeneratorBlockEntity {
        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition,
            BlockState pBlockState) {
            super(MachineCapacitorKeys.DEV_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Standard;
        }
    }

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
    protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
        // TODO: Really not a fan of how this works. Review this in my next PR... I kinda want to put slot validation into ItemSlotLayout maybe?
        return new ItemHandlerMaster(getIoConfig(), layout) {
            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                // Check its a capacitor.
                if (slot == 1 && !CapacitorUtil.isCapacitor(stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
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
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StirlingGeneratorMenu(this, pInventory, pContainerId);
    }
}
