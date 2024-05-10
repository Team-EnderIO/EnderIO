package com.enderio.conduits.common.types.energy;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.core.CoreNBTKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class EnergyExtendedData implements ExtendedConduitData<EnergyExtendedData> {

    private final Map<Direction, EnergySidedData> energySidedData = new EnumMap<>(Direction.class);


    private int capacity = 500;
    private int stored = 0;

    private IEnergyStorage selfCap = new EnergyExtendedData.ConduitEnergyStorage(this);


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        for (Direction direction: Direction.values()) {
            @Nullable EnergySidedData sidedData = energySidedData.get(direction);
            if (sidedData != null) {
                tag.put(direction.name(), sidedData.toNbt());
            }
        }
        tag.putInt(CoreNBTKeys.ENERGY_MAX_STORED, capacity);
        tag.putInt(CoreNBTKeys.ENERGY_STORED, stored);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        energySidedData.clear();
        for (Direction direction: Direction.values()) {
            if (nbt.contains(direction.name())) {
                energySidedData.put(direction, EnergySidedData.fromNbt(nbt.getCompound(direction.name())));
            }
        }
        if (nbt.contains(CoreNBTKeys.ENERGY_MAX_STORED)) {
            capacity = Math.max(nbt.getInt(CoreNBTKeys.ENERGY_MAX_STORED), 500);
        }
        if (nbt.contains(CoreNBTKeys.ENERGY_STORED)) {
            stored = nbt.getInt(CoreNBTKeys.ENERGY_STORED);
        }
    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getStored() {
        return stored;
    }

    public void setStored(int stored) {
        this.stored = stored;
    }

    @Override
    public void onRemoved(ConduitType<?> type, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    public EnergySidedData compute(Direction direction) {
        return energySidedData.computeIfAbsent(direction, dir -> new EnergySidedData());
    }

    IEnergyStorage getSelfCap() {
        if (selfCap == null) {
            selfCap = new EnergyExtendedData.ConduitEnergyStorage(this);
        }

        return selfCap;
    }

    public static class EnergySidedData {
        public int rotatingIndex = 0;

        // region Serialization

        private static final String KEY_ROTATING_INDEX = "RotatingIndex";

        private CompoundTag toNbt() {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt(KEY_ROTATING_INDEX, rotatingIndex);
            return nbt;
        }

        private static EnergySidedData fromNbt(CompoundTag nbt) {
            EnergySidedData sidedData = new EnergySidedData();
            if (nbt.contains(KEY_ROTATING_INDEX, Tag.TAG_INT)) {
                sidedData.rotatingIndex = nbt.getInt(KEY_ROTATING_INDEX);
            }

            return sidedData;
        }

        // endregion
    }

    private record ConduitEnergyStorage(EnergyExtendedData data) implements IEnergyStorage {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int receivable = Math.min(data.getCapacity() - data().getStored(), maxReceive);
            if (!simulate) {
                data.setStored(data.getStored()+receivable);
            }
            return receivable;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extractable = Math.min(data().getStored(), maxExtract);
            if (!simulate) {
                data.setStored(data.getStored() - extractable);
            }
            return extractable;
        }

        @Override
        public int getEnergyStored() {
            return data.getStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return data().getCapacity();
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
