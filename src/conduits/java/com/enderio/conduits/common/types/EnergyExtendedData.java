package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IExtendedConduitData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class EnergyExtendedData implements IExtendedConduitData<EnergyExtendedData> {

    private final Map<Direction, EnergySidedData> energySidedData = new EnumMap<>(Direction.class);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (Direction direction: Direction.values()) {
            @Nullable EnergySidedData sidedData = energySidedData.get(direction);
            if (sidedData != null) {
                tag.put(direction.name(), sidedData.toNbt());
            }
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (Direction direction: Direction.values()) {
            if (nbt.contains(direction.name())) {
                energySidedData.put(direction, EnergySidedData.fromNbt(nbt.getCompound(direction.name())));
            }
        }
    }

    public EnergySidedData get(Direction direction) {
        return energySidedData.getOrDefault(direction, new EnergySidedData());
    }
    public EnergySidedData compute(Direction direction) {
        return energySidedData.computeIfAbsent(direction, dir -> new EnergySidedData());
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
            if (nbt.contains(KEY_ROTATING_INDEX))
                sidedData.rotatingIndex= nbt.getInt(KEY_ROTATING_INDEX);
            return sidedData;
        }

        // endregion
    }
}
