package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IExtendedConduitData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class ItemExtendedData implements IExtendedConduitData<ItemExtendedData> {

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

    public static class ItemSidedData {
        public boolean roundRobin = false;
        public int rotatingIndex = 0;
        public boolean selfFeed = false;
        public int priority = 0;

        private CompoundTag toNbt() {
            CompoundTag nbt = toGuiNbt();
            nbt.putInt("rotating_index", rotatingIndex);
            return nbt;
        }

        private CompoundTag toGuiNbt() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("rr", roundRobin);
            nbt.putBoolean("sf", selfFeed);
            nbt.putInt("prio", priority);
            return nbt;
        }

        private static ItemSidedData fromNbt(CompoundTag nbt) {
            ItemSidedData sidedData = new ItemSidedData();
            sidedData.roundRobin = nbt.getBoolean("rr");
            sidedData.selfFeed = nbt.getBoolean("sf");
            sidedData.priority= nbt.getInt("prio");
            if (nbt.contains("rotating_index"))
                sidedData.rotatingIndex= nbt.getInt("rotating_index");
            return sidedData;
        }
    }
}
