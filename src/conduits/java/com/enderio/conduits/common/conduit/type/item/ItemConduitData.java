package com.enderio.conduits.common.conduit.type.item;

import com.enderio.api.conduit.ConduitData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class ItemConduitData implements ConduitData<ItemConduitData> {

    private final Map<Direction, ItemSidedData> itemSidedData = new EnumMap<>(Direction.class);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (Direction direction: Direction.values()) {
            @Nullable
            ItemSidedData sidedData = itemSidedData.get(direction);
            if (sidedData != null) {
                tag.put(direction.name(), sidedData.toNbt());
            }
        }
        return tag;
    }

    @Override
    public CompoundTag serializeGuiNBT() {
        CompoundTag tag = new CompoundTag();
        for (Direction direction: Direction.values()) {
            @Nullable
            ItemSidedData sidedData = itemSidedData.get(direction);
            if (sidedData != null) {
                tag.put(direction.name(), sidedData.toGuiNbt());
            }
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (Direction direction: Direction.values()) {
            if (nbt.contains(direction.name())) {
                itemSidedData.put(direction, ItemSidedData.fromNbt(nbt.getCompound(direction.name())));
            }
        }
    }

    public ItemSidedData get(Direction direction) {
        return itemSidedData.getOrDefault(direction, new ItemSidedData());
    }
    public ItemSidedData compute(Direction direction) {
        return itemSidedData.computeIfAbsent(direction, dir -> new ItemSidedData());
    }

    @Override
    public int hashCode() {
        return itemSidedData.hashCode();
    }

    public static class ItemSidedData {
        public boolean isRoundRobin = false;
        public int rotatingIndex = 0;
        public boolean isSelfFeed = false;
        public int priority = 0;

        @Override
        public int hashCode() {
            return Objects.hash(isRoundRobin, isSelfFeed, priority);
        }

        // region Serialization

        private static final String KEY_ROTATING_INDEX = "RotatingIndex";
        private static final String KEY_ROUND_ROBIN = "RoundRobin";
        private static final String KEY_SELF_FEED = "SelfFeed";
        private static final String KEY_PRIORITY = "Priority";

        private CompoundTag toNbt() {
            CompoundTag nbt = toGuiNbt();
            nbt.putInt(KEY_ROTATING_INDEX, rotatingIndex);
            return nbt;
        }

        private CompoundTag toGuiNbt() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean(KEY_ROUND_ROBIN, isRoundRobin);
            nbt.putBoolean(KEY_SELF_FEED, isSelfFeed);
            nbt.putInt(KEY_PRIORITY, priority);
            return nbt;
        }

        private static ItemSidedData fromNbt(CompoundTag nbt) {
            ItemSidedData sidedData = new ItemSidedData();
            sidedData.isRoundRobin = nbt.getBoolean(KEY_ROUND_ROBIN);
            sidedData.isSelfFeed = nbt.getBoolean(KEY_SELF_FEED);
            sidedData.priority = nbt.getInt(KEY_PRIORITY);
            if (nbt.contains(KEY_ROTATING_INDEX)) {
                sidedData.rotatingIndex = nbt.getInt(KEY_ROTATING_INDEX);
            }

            return sidedData;
        }

        // endregion
    }
}
