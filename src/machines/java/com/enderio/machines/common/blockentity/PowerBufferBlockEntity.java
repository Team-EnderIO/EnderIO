package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.CoreNBTKeys;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.core.common.network.slot.StringNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.PowerBufferMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PowerBufferBlockEntity extends PoweredMachineBlockEntity {
    int maxInput = 0;
    protected IntegerNetworkDataSlot inputDataSlot;

    int maxOutput = 0;
    protected IntegerNetworkDataSlot outputDataSlot;

    // Stores textbox value before being converted to energy value
    protected StringNetworkDataSlot inputTextDataSlot;
    String inputTextValue = "0";
    protected StringNetworkDataSlot outputTextDataSlot;
    String outputTextValue = "0";

    //todo: energy balancing
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.POWER_BUFFER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.POWER_BUFFER_USAGE);

    public PowerBufferBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Both, CAPACITY, USAGE, type, worldPosition, blockState);

        inputDataSlot = new IntegerNetworkDataSlot(this::getMaxInput, input -> maxInput = input);
        outputDataSlot = new IntegerNetworkDataSlot(this::getMaxOutput, output -> maxOutput = output);
        addDataSlot(inputDataSlot);
        addDataSlot(outputDataSlot);

        inputTextDataSlot = new StringNetworkDataSlot(this::getMaxInputText, input -> inputTextValue = input);
        outputTextDataSlot = new StringNetworkDataSlot(this::getMaxOutputText, output -> outputTextValue = output);
        addDataSlot(inputTextDataSlot);
        addDataSlot(outputTextDataSlot);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout
            .builder()
            .capacitor()
            .build();
    }

    @Override
    protected boolean isActive() {
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PowerBufferMenu(this, playerInventory, containerId);
    }

    public void setMaxOutput(int pMaxOutput) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(outputDataSlot, pMaxOutput);
        } else this.maxOutput = pMaxOutput;
    }

    public void setMaxInput(int pMaxInput) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(inputDataSlot, pMaxInput);
        } else this.maxInput = pMaxInput;
    }

    public void setMaxInputText(String maxInputText) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(inputTextDataSlot, maxInputText);
        } this.inputTextValue = maxInputText;
    }

    public void setMaxOutputText(String maxOutputText) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(outputTextDataSlot, maxOutputText);
        } this.outputTextValue = maxOutputText;
    }

    public String getMaxInputText() {
        return this.inputTextValue;
    }

    public String getMaxOutputText() {
        return this.outputTextValue;
    }

    public int getMaxInput() {
        return maxInput;
    }

    public int getMaxOutput() {
        return maxOutput;
    }

    public List<Direction> getDirections(boolean input) {
        List<Direction> dirs = new ArrayList<>();

        if (!getExposedEnergyStorage().getIOMode().canOutput() && !input)
            return new ArrayList<>();

        for (Direction side : Direction.values()) {

            if (!shouldPushEnergyTo(side) && !input)
                continue;

            getCapability(ForgeCapabilities.ENERGY, side).resolve().ifPresent(selfHandler -> {
                Optional<IEnergyStorage> otherHandler = getNeighbouringCapability(ForgeCapabilities.ENERGY, side).resolve();
                if (otherHandler.isPresent()) {

                    if (input) {
                        if (otherHandler.get().canExtract()) {
                            dirs.add(side);
                        }
                    } else {
                        if (otherHandler.get().canReceive()) {
                            dirs.add(side);
                        }
                    }
                }
            });
        }
        return dirs;
    }

    //Distributes energy equally between sides (maxEnergy / number of sides)
    @Override
    public void pushEnergy() {
        if (!getExposedEnergyStorage().getIOMode().canOutput())
            return;


        List<Direction> directions = getDirections(false);
        int energyPerSide = directions.size() > 0 ? (int)getMaxOutput()/directions.size() : 0;

        for (int i = 0; i < directions.size(); i++) {

            //if last iteration, add remaining energy (for odd number of sides)
            if (i == directions.size() -1 ) {
                energyPerSide = energyPerSide + (getMaxOutput() - (energyPerSide * directions.size()));
            }

            Direction side = directions.get(i);
            Optional<IEnergyStorage> otherHandler = getNeighbouringCapability(ForgeCapabilities.ENERGY, side).resolve();

            if (otherHandler.isPresent()) {

                // If the other handler can receive power transmit ours
                if (otherHandler.get().canReceive() && getExposedEnergyStorage().getEnergyStored() >= 0) {

                    int received = otherHandler.get().receiveEnergy(Math.min(energyPerSide, getExposedEnergyStorage().getEnergyStored()), false);
                    // Consume that energy from our buffer.
                    getExposedEnergyStorage().extractEnergy(received, false);
                }
            }
        }
    }

    @Override
    protected boolean shouldPushEnergyTo(Direction direction) {
        return getIOConfig().getMode(direction).canPush();
    }

    @Override
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity, Supplier<Integer> usageRate) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, capacity, usageRate) {

            //helps to distribute energy input
            int sideReceived = 0;

            @Override
            public boolean canExtract() {
                return super.canExtract() && canAct();
            }

            @Override
            public boolean canReceive() {
                return super.canReceive() && canAct();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                List<Direction> dirs = getDirections(true);
                int energyToReceive = getMaxInput() / dirs.size();

                if (sideReceived == 0) {
                    energyToReceive += getMaxInput() % dirs.size();
                }

                int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(energyToReceive, maxReceive));

                if (!simulate) {
                    addEnergy(energyReceived);
                    sideReceived = (sideReceived + 1) % dirs.size();
                }

                return energyReceived;
            }
        };
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt(CoreNBTKeys.BUFFER_MAX_INPUT, maxInput);
        pTag.putInt(CoreNBTKeys.BUFFER_MAX_OUTPUT, maxOutput);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        maxInput = pTag.getInt(CoreNBTKeys.BUFFER_MAX_INPUT);
        maxOutput = pTag.getInt(CoreNBTKeys.BUFFER_MAX_OUTPUT);
    }
}
