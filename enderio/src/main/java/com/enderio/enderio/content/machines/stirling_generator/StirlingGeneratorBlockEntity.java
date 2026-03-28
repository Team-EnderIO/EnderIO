package com.enderio.enderio.content.machines.stirling_generator;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.FixedScalable;
import com.enderio.enderio.api.capacitor.LinearScalable;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.capacitor.SteppedScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class StirlingGeneratorBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_CAPACITY);

    public static final SteppedScalable FUEL_EFFICIENCY = new SteppedScalable(CapacitorModifier.FUEL_EFFICIENCY,
            MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_FUEL_EFFICIENCY_BASE,
            MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_FUEL_EFFICIENCY_STEP);

    public static final LinearScalable GENERATION_SPEED = new LinearScalable(
            CapacitorModifier.BURNING_ENERGY_GENERATION, MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_PRODUCTION);

    public static final SingleResourceSlotKey<ItemResource> FUEL = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private int burnTime;
    private int burnDuration;

    public StirlingGeneratorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.STIRLING_GENERATOR.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Output, CAPACITY, FixedScalable.ZERO);
    }

    public int getGenerationRate() {
        return GENERATION_SPEED.scaleI(this::getCapacitorData).get();
    }

    public int getFuelEfficiency() {
        return FUEL_EFFICIENCY.scaleI(this::getCapacitorData).get();
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(FUEL, SlotTemplates.input(), b -> b
                .filter((_, resource) -> resource.toStack().getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0
                    && resource.toStack().getCraftingRemainder() == null))
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    @Override
    protected void onEnergyChanged(int previousAmount) {
        super.onEnergyChanged(previousAmount);

        updateMachineState(MachineState.FULL_POWER,
            (getEnergyStorage().getAmountAsInt() >= getEnergyStorage().getCapacityAsInt())
                && isCapacitorInstalled());
    }

    @Override
    public void serverTick() {
        // We ignore redstone control here.
        if (isGenerating()) {
            burnTime--;

            if (!requiresCapacitor() || isCapacitorInstalled()) {
                getEnergyStorage().add(getGenerationRate(), null);
            }
        }

        // Taking more fuel is locked behind redstone control.
        if (canAct()) {
            if (!isGenerating() && !EnergyHandlerUtil.isFull(getEnergyStorage())) {
                // Get the fuel
                ItemStack fuel = getInventory().getStack(FUEL);
                if (!fuel.isEmpty()) {
                    // Get the burn time.
                    int burningTime = fuel.getBurnTime(RecipeType.SMELTING, level.fuelValues());

                    if (burningTime > 0) {
                        float burnSpeed = MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_BURN_SPEED.get().floatValue();
                        float efficiency = getFuelEfficiency() / 100.0f;

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
    public boolean isActive() {
        return canAct() && hasEnergy() && isGenerating();
    }

    public boolean isGenerating() {
        if (level == null) {
            return false;
        }

        return burnTime > 0;
    }

    public float getBurnProgress() {
        if (burnDuration != 0) {
            return burnTime / (float) burnDuration;
        }

        return 0;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new StirlingGeneratorMenu(containerId, inventory, this);
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        if (FUEL.index(getInventory()) == slot) {
            updateMachineState(MachineState.EMPTY_INPUT, getInventory().getStack(FUEL).isEmpty());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.burnTime = input.getIntOr(MachineNBTKeys.BURN_TIME, 0);
        this.burnDuration = input.getIntOr(MachineNBTKeys.BURN_DURATION, 0);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER, EnergyHandlerUtil.isFull(getEnergyStorage()) && isCapacitorInstalled());
        updateMachineState(MachineState.EMPTY_INPUT, getInventory().getStack(FUEL).isEmpty());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt(MachineNBTKeys.BURN_TIME, burnTime);
        output.putInt(MachineNBTKeys.BURN_DURATION, burnDuration);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER,
                EnergyHandlerUtil.isFull(getEnergyStorage()) && isCapacitorInstalled());
        updateMachineState(MachineState.EMPTY_INPUT, getInventory().getStack(FUEL).isEmpty());
    }
}
