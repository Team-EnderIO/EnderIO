package com.enderio.machines.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.blockentity.sync.FloatDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PowerGeneratingMachineEntity;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

public abstract class StirlingGeneratorBlockEntity extends PowerGeneratingMachineEntity {
    public static class Simple extends StirlingGeneratorBlockEntity {
        public Simple(BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState) {
            super(MachineCapacitorKeys.SIMPLE_STIRLING_GENERATOR_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.SIMPLE;
        }

        @Override
        public int getEnergyLeakPerSecond() {
            return 2;
        }
    }

    public static class Standard extends StirlingGeneratorBlockEntity {
        public Standard(BlockEntityType<?> type, BlockPos worldPosition,
            BlockState blockState) {
            super(MachineCapacitorKeys.STIRLING_GENERATOR_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.STANDARD;
        }
    }

    private int burnTime;
    private int burnDuration;

    @UseOnly(LogicalSide.CLIENT)
    private float clientBurnProgress;

    public StirlingGeneratorBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> type, BlockPos worldPosition,
        BlockState blockState) {
        super(capacityKey, transferKey, consumptionKey, type, worldPosition, blockState);

        addDataSlot(new FloatDataSlot(this::getBurnProgress, p -> clientBurnProgress = p, SyncMode.GUI));
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder(getTier() != MachineTier.SIMPLE)
            .inputSlot((slot, stack) -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0)
            .build();
    }

    @Override
    public void serverTick() {
        // Tick burn time even if redstone activation has stopped.
        if (isGenerating()) {
            burnTime--;
        }

        // Only continue burning if redstone is enabled and the internal buffer has space.
        if (canAct() && !isGenerating() && getEnergyStorage().getEnergyStored() < getEnergyStorage().getMaxEnergyStored()) {
            // Get the fuel
            ItemStack fuel = getInventory().getStackInSlot(0);
            if (!fuel.isEmpty()) {
                // Get the burn time.
                int burningTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);

                if (burningTime > 0) {
                    burnTime = burningTime;
                    burnDuration = burnTime;

                    // Remove the fuel
                    fuel.shrink(1);
                    getInventory().setStackInSlot(0, fuel);
                }
            }
        }

        super.serverTick();
    }

    @Override
    public boolean isGenerating() {
        return burnTime > 0;
    }

    public float getBurnProgress() {
        if (level.isClientSide)
            return clientBurnProgress;
        if (burnDuration == 0)
            return 0;
        return burnTime / (float) burnDuration;
    }

    @Override
    public int getGenerationRate() {
        // Stirling generator produces 10 RF per tick of burn time.
        // https://github.com/SleepyTrousers/EnderIO/blob/d6dfb9d3964946ceb9fd72a66a3cff197a51a1fe/enderio-base/src/main/java/crazypants/enderio/base/recipe/alloysmelter/VanillaSmeltingRecipe.java#L50
        // TODO: Should maybe have a constants class for energy conversions.
        // TODO: Implement efficiency!
        return 10;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StirlingGeneratorMenu(this, pInventory, pContainerId);
    }
}
