package com.enderio.enderio.content.machines.stirling_generator;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.FixedScalable;
import com.enderio.enderio.api.capacitor.LinearScalable;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.capacitor.SteppedScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.client.SoundHandler;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.energy.MachineEnergyStorage;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StirlingGeneratorBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_CAPACITY);

    public static final SteppedScalable FUEL_EFFICIENCY = new SteppedScalable(CapacitorModifier.FUEL_EFFICIENCY,
            MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_FUEL_EFFICIENCY_BASE,
            MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_FUEL_EFFICIENCY_STEP);

    public static final LinearScalable GENERATION_SPEED = new LinearScalable(
            CapacitorModifier.BURNING_ENERGY_GENERATION, MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_PRODUCTION);

    public static final SingleSlotAccess FUEL = new SingleSlotAccess();

    private int burnTime;
    private int burnDuration;

    @UseOnly(LogicalSide.CLIENT)
    @Nullable
    private SoundInstance sound;

    public StirlingGeneratorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.STIRLING_GENERATOR.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Output, CAPACITY, FixedScalable.ZERO);
    }

    public int getGenerationRate() {
        return GENERATION_SPEED.scaleI(this::getCapacitorData).get();
    }

    public int getFuelEfficiency() {
        return FUEL_EFFICIENCY.scaleI(this::getCapacitorData).get();
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot((slot, stack) -> stack.getBurnTime(RecipeType.SMELTING) > 0
                        && stack.getCraftingRemainingItem().isEmpty())
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
                getEnergyStorage().addEnergy(getGenerationRate());
            }
        }

        // Taking more fuel is locked behind redstone control.
        if (canAct()) {
            if (!isGenerating() && getEnergyStorage().getEnergyStored() < getEnergyStorage().getMaxEnergyStored()) {
                // Get the fuel
                ItemStack fuel = FUEL.getItemStack(this);
                if (!fuel.isEmpty()) {
                    // Get the burn time.
                    int burningTime = fuel.getBurnTime(RecipeType.SMELTING);

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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ProgressMachineBlock.POWERED)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            if (sound == null || !SoundHandler.isActive(sound)) {
                sound = new SimpleSoundInstance(EIOSounds.STIRLING.get(), SoundSource.BLOCKS, 1.0f, 1.0f, random, x, y, z);
                SoundHandler.playSound(sound);
            }

            Direction direction = state.getValue(ProgressMachineBlock.FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.52;
            double ss = random.nextDouble() * 0.6 - 0.3;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * r : ss;
            double dy = random.nextDouble() * 6.0 / 16.0;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * r : ss;
            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
        } else if (sound != null && SoundHandler.isActive(sound)) {
            SoundHandler.stopSound(sound);
            sound = null;
        }
    }

    @Override
    public boolean isActive() {
        return canAct() && isGenerating();
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
        if (FUEL.isSlot(slot)) {
            updateMachineState(MachineState.EMPTY_INPUT, FUEL.getItemStack(this).isEmpty());
        }
    }

    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity,
            Supplier<Integer> usageRate) {
        return new MachineEnergyStorage(this, energyIOMode, capacity, usageRate) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                updateMachineState(MachineState.FULL_POWER,
                        (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored())
                                && isCapacitorInstalled());
            }
        };
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (isActive()) {
            updateMachineState(MachineState.EMPTY_INPUT, false);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);

        if (tag.contains(MachineNBTKeys.BURN_TIME, CompoundTag.TAG_INT)) {
            burnTime = tag.getInt(MachineNBTKeys.BURN_TIME);
        }

        if (tag.contains(MachineNBTKeys.BURN_DURATION, CompoundTag.TAG_INT)) {
            burnDuration = tag.getInt(MachineNBTKeys.BURN_DURATION);
        }

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER,
                (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored())
                        && isCapacitorInstalled());
        updateMachineState(MachineState.EMPTY_INPUT, FUEL.getItemStack(this).isEmpty());
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);

        tag.putInt(MachineNBTKeys.BURN_TIME, burnTime);
        tag.putInt(MachineNBTKeys.BURN_DURATION, burnDuration);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER,
                (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored())
                        && isCapacitorInstalled());
        updateMachineState(MachineState.EMPTY_INPUT, FUEL.getItemStack(this).isEmpty());
    }

    @Override
    protected void updatePowerState() {

    }
}
