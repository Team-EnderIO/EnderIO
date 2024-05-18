package com.enderio.machines.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.FixedScalable;
import com.enderio.api.capacitor.LinearScalable;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.capacitor.SteppedScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.FloatNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StirlingGeneratorBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_CAPACITY);

    public static final SteppedScalable FUEL_EFFICIENCY = new SteppedScalable(
        CapacitorModifier.FUEL_EFFICIENCY,
        MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_FUEL_EFFICIENCY_BASE,
        MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_FUEL_EFFICIENCY_STEP);

    public static final LinearScalable GENERATION_SPEED = new LinearScalable(
        CapacitorModifier.BURNING_ENERGY_GENERATION,
        MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_PRODUCTION);

    public static final SingleSlotAccess FUEL = new SingleSlotAccess();

    private int burnTime;
    private int burnDuration;

    @UseOnly(LogicalSide.CLIENT)
    private float clientBurnProgress;

    public StirlingGeneratorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition,
        BlockState blockState) {
        super(EnergyIOMode.Output, CAPACITY, FixedScalable.ZERO, type, worldPosition, blockState);
        addDataSlot(new FloatNetworkDataSlot(this::getBurnProgress, p -> clientBurnProgress = p));
    }

    public int getGenerationRate() {
        return GENERATION_SPEED.scaleI(this::getCapacitorData).get();
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot((slot, stack) -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0 && stack.getCraftingRemainingItem().isEmpty())
            .slotAccess(FUEL)
            .capacitor()
            .build();
    }

    @Override
    public void serverTick() {
        // We ignore redstone control here.
        if (isGenerating()) {
            burnTime--;

            if (!requiresCapacitor() || isCapacitorInstalled()) {
                energyStorage.addEnergy(getGenerationRate());
            }
        }

        // Taking more fuel is locked behind redstone control.
        if (canAct()) {
            if (!isGenerating() && getEnergyStorage().getEnergyStored() < getEnergyStorage().getMaxEnergyStored()) {
                // Get the fuel
                ItemStack fuel = FUEL.getItemStack(this);
                if (!fuel.isEmpty()) {
                    // Get the burn time.
                    int burningTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);

                    if (burningTime > 0) {
                        float burnSpeed = MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_BURN_SPEED.get().floatValue();
                        float efficiency = FUEL_EFFICIENCY.scaleF(this::getCapacitorData).get() / 100.0f;

                        burnTime = (int) Math.floor(burningTime * burnSpeed * efficiency);
                        burnDuration = burnTime;

                        // Remove the fuel
                        fuel.shrink(1);
                    }
                }
            }
        }

        super.serverTick();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && isGenerating();
    }

    public boolean isGenerating() {
        if (level == null) {
            return false;
        }

        return burnTime > 0;
    }

    public float getBurnProgress() {
        if (level != null && level.isClientSide) {
            return clientBurnProgress;
        }

        if (burnDuration != 0) {
            return burnTime / (float) burnDuration;
        }

        return 0;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StirlingGeneratorMenu(this, pInventory, pContainerId);
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        if (FUEL.isSlot(slot)) {
            updateMachineState(MachineState.EMPTY_INPUT, FUEL.getItemStack(getInventoryNN()).isEmpty());
        }
    }

    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity, Supplier<Integer> usageRate) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, capacity, usageRate) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                updateMachineState(MachineState.FULL_POWER, (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
            }
        };
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER, (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
        updateMachineState(MachineState.EMPTY_INPUT, FUEL.getItemStack(getInventoryNN()).isEmpty());
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER, (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
        updateMachineState(MachineState.EMPTY_INPUT, FUEL.getItemStack(getInventoryNN()).isEmpty());
    }
}
