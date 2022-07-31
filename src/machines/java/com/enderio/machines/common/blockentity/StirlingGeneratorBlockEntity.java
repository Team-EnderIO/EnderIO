package com.enderio.machines.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.core.common.sync.FloatDataSlot;
import com.enderio.core.common.sync.SyncMode;
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

public class StirlingGeneratorBlockEntity extends PowerGeneratingMachineEntity {
    private int burnTime;
    private int burnDuration;

    @UseOnly(LogicalSide.CLIENT)
    private float clientBurnProgress;

    public StirlingGeneratorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition,
        BlockState blockState) {
        super(MachineCapacitorKeys.STIRLING_GENERATOR_ENERGY_CAPACITY.get(),
            MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
            MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
            type, worldPosition, blockState);

        addDataSlot(new FloatDataSlot(this::getBurnProgress, p -> clientBurnProgress = p, SyncMode.GUI));
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder(true)
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
