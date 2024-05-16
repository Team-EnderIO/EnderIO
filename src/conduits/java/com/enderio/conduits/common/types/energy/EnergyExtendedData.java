package com.enderio.conduits.common.types.energy;

import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.CoreNBTKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    private final Map<Direction, EnergySidedData> energySidedData;

    private int capacity = 500;
    private int stored = 0;

    public EnergyExtendedData() {
        this.energySidedData = new EnumMap<>(Direction.class);
    }

    private EnergyExtendedData(Map<Direction, EnergySidedData> energySidedData, int capacity, int stored) {
        this.energySidedData = energySidedData;
        this.capacity = capacity;
        this.stored = stored;
    }

    private IEnergyStorage selfCap = new EnergyExtendedData.ConduitEnergyStorage(this);

    @Override
    public void applyGuiChanges(EnergyExtendedData guiData) {
    }

    @Override
    public ConduitDataSerializer<EnergyExtendedData> serializer() {
        return ConduitTypes.ENERGY_DATA_SERIALIZER.get();
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
    public void onRemoved(ConduitType<EnergyExtendedData> type, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    public EnergySidedData compute(Direction direction) {
        return energySidedData.computeIfAbsent(direction, dir -> new EnergySidedData(0));
    }

    IEnergyStorage getSelfCap() {
        if (selfCap == null) {
            selfCap = new EnergyExtendedData.ConduitEnergyStorage(this);
        }

        return selfCap;
    }

    public static class EnergySidedData {

        public static Codec<EnergySidedData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.INT.fieldOf("rotating_index").forGetter(i -> i.rotatingIndex)
            ).apply(instance, EnergySidedData::new)
        );

        public int rotatingIndex;

        public EnergySidedData(int rotatingIndex) {
            this.rotatingIndex = rotatingIndex;
        }
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

    public static class Serializer implements ConduitDataSerializer<EnergyExtendedData> {

        public static MapCodec<EnergyExtendedData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.unboundedMap(Direction.CODEC, EnergySidedData.CODEC)
                    .fieldOf("energy_sided_data")
                    .forGetter(e -> e.energySidedData),
                Codec.INT.fieldOf("capacity").forGetter(EnergyExtendedData::getCapacity),
                Codec.INT.fieldOf("stored").forGetter(EnergyExtendedData::getStored)
            ).apply(instance, EnergyExtendedData::new)
        );

        @Override
        public MapCodec<EnergyExtendedData> codec() {
            return CODEC;
        }
    }
}
