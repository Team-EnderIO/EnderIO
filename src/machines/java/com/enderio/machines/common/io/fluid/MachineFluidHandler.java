package com.enderio.machines.common.io.fluid;

import com.enderio.EnderIO;
import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.api.io.IIOConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * MachineFluidStorage takes a list of fluid tanks and handles IO for them all.
 */
public class MachineFluidHandler implements IFluidHandler, IEnderCapabilityProvider<IFluidHandler>, INBTSerializable<CompoundTag> {

    public static final String TANK_INDEX = "Index";
    public static final String TANKS = "Tanks";
    public static final String TANK_LIST_SIZE = "Size";

    private final IIOConfig config;
    private final MachineTankLayout layout;
    private List<MachineFluidTank> tanks;

    private final EnumMap<Direction, LazyOptional<Sided>> sideCache = new EnumMap<>(Direction.class);
    private LazyOptional<MachineFluidHandler> selfCache = LazyOptional.empty();

    // Not sure if we need this but might be useful to update recipe/task if tank is filled.
    private IntConsumer changeListener = i -> {};

    public MachineFluidHandler(IIOConfig config, MachineTankLayout layout) {
        this.config = config;
        this.layout = layout;
        this.tanks = layout.createTanks();
    }

    public void addSlotChangedCallback(IntConsumer callback) {
        changeListener = changeListener.andThen(callback);
    }

    public final IIOConfig getConfig() {
        return config;
    }

    public MachineTankLayout getLayout() {
        return layout;
    }

    //Not a good idea to use this method. Tank Access should be the way to access tanks
    @Deprecated
    public final MachineFluidTank getTank(int tank) {
        return tanks.get(tank);
    }

    @Override
    public int getTanks() {
        return layout.getTankCount();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks.get(tank).getFluid();
    }

    public void setFluidInTank(int tank, FluidStack fluid) {
        tanks.get(tank).setFluid(fluid);
    }

    @Override
    public int getTankCapacity(int tank) {
        return layout.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return layout.isFluidValid(tank, stack);
    }

    @Override
    public Capability<IFluidHandler> getCapabilityType() {
        return ForgeCapabilities.FLUID_HANDLER;
    }

    @Override
    public LazyOptional<IFluidHandler> getCapability(@Nullable Direction side) {
        if (side == null) {
            // Create own cache if its been invalidated or not created yet.
            if (!selfCache.isPresent()) {
                selfCache = LazyOptional.of(() -> this);
            }

            return selfCache.cast();
        }

        if (!config.getMode(side).canConnect()) {
            return LazyOptional.empty();
        }

        return sideCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new Sided(this, dir))).cast();
    }

    @Override
    public void invalidateSide(@Nullable Direction side) {
        if (side != null) {
            if (sideCache.containsKey(side)) {
                sideCache.get(side).invalidate();
                sideCache.remove(side);
            }
        } else {
            selfCache.invalidate();
        }
    }

    @Override
    public void invalidateCaps() {
        for (LazyOptional<Sided> side : sideCache.values()) {
            side.invalidate();
        }
        selfCache.invalidate();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        // Don't waste any time.
        if (resource.isEmpty()) {
            return 0;
        }

        // Copy the fluid stack and prepare to distribute it across all tanks
        FluidStack resourceLeft = resource.copy();
        int totalFilled = 0;

        for (int index = 0; index < tanks.size(); index++) {
            if (!layout.canInsert(index))
                continue;

            // Attempt to fill the tank
            int filled = tanks.get(index).fill(resourceLeft, action);
            resourceLeft.shrink(filled);
            totalFilled += filled;

            if (filled > 0) {
                onContentsChanged(index);
            }

            // If we used up all the resource, stop trying.
            if (resourceLeft.isEmpty()) {
                break;
            }

        }
        return totalFilled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        for (int index = 0; index < tanks.size(); index++) {
            if (!layout.canExtract(index))
                continue;

            if (tanks.get(index).drain(resource, FluidAction.SIMULATE) != FluidStack.EMPTY) {
                FluidStack drained = tanks.get(index).drain(resource, action);
                if (!drained.isEmpty()) {
                    onContentsChanged(index);
                    changeListener.accept(index);
                }
                return drained;
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (int index = 0; index < tanks.size(); index++) {
            if (tanks.get(index).drain(maxDrain, FluidAction.SIMULATE) != FluidStack.EMPTY) {
                FluidStack drained = tanks.get(index).drain(maxDrain, action);
                if (!drained.isEmpty()) {
                    onContentsChanged(index);
                    changeListener.accept(index);
                }
                return drained;
            }
        }

        return FluidStack.EMPTY;
    }

    protected void onContentsChanged(int slot) {}

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < tanks.size(); i++) {
            CompoundTag tankTag = new CompoundTag();
            tankTag.putInt(TANK_INDEX, i);
            tanks.get(i).save(tankTag);
            nbtTagList.add(tankTag);
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put(TANKS, nbtTagList);
        nbt.putInt(TANK_LIST_SIZE, tanks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // Assume old NBT format.
        if (!nbt.contains(TANK_LIST_SIZE) && !nbt.contains(TANKS) && !nbt.isEmpty()) {
            if (tanks.size() > 1) {
                int capacity = layout.getTankCapacity(0);
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(nbt);
                tanks.set(0, new MachineFluidTank(fluidStack, capacity));
            } else {
                EnderIO.LOGGER.warn("Failed to load MachineFluidHandler tank contents.");
            }
        }

        int size = nbt.contains(TANK_LIST_SIZE, Tag.TAG_INT) ? nbt.getInt(TANK_LIST_SIZE) : tanks.size();
        tanks = NonNullList.withSize(size, MachineFluidTank.EMPTY);
        ListTag tagList = nbt.getList(TANKS, Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag tankTag = tagList.getCompound(i);
            int index = tankTag.getInt(TANK_INDEX);
            tanks.set(index, MachineFluidTank.from(tankTag));
        }
    }

    // Sided capability access
    private record Sided(MachineFluidHandler master, Direction direction) implements IFluidHandler {
        @Override
        public int getTanks() {
            return master.getTanks();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return master.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return master.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return master.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (master.getConfig().getMode(direction).canInput()) {
                return master.fill(resource, action);
            }

            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (master.getConfig().getMode(direction).canOutput()) {
                return master.drain(resource, action);
            }

            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (master.getConfig().getMode(direction).canOutput()) {
                return master.drain(maxDrain, action);
            }

            return FluidStack.EMPTY;
        }
    }
}
