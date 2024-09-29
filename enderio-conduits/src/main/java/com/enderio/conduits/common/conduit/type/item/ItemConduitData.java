package com.enderio.conduits.common.conduit.type.item;

import com.enderio.conduits.api.ConduitDataType;
import com.enderio.conduits.api.ConduitData;
import com.enderio.conduits.common.init.ConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemConduitData implements ConduitData<ItemConduitData> {

    public static MapCodec<ItemConduitData> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.unboundedMap(Direction.CODEC, ItemSidedData.CODEC)
                .fieldOf("item_sided_data").forGetter(i -> i.itemSidedData)
        ).apply(instance, ItemConduitData::new)
    );

    public static StreamCodec<RegistryFriendlyByteBuf, ItemConduitData> STREAM_CODEC =
        ByteBufCodecs.map(i -> (Map<Direction, ItemSidedData>) new HashMap<Direction, ItemSidedData>(i),
                Direction.STREAM_CODEC, ItemSidedData.STREAM_CODEC)
            .map(ItemConduitData::new, i -> i.itemSidedData).cast();

    public Map<Direction, ItemSidedData> itemSidedData;

    public ItemConduitData() {
        itemSidedData = new HashMap<>(Direction.values().length);
    }

    public ItemConduitData(Map<Direction, ItemSidedData> itemSidedData) {
        this.itemSidedData = new HashMap<>(itemSidedData);
    }

    @Override
    public ItemConduitData withClientChanges(ItemConduitData guiData) {
        for (Direction direction : Direction.values()) {
            compute(direction).applyGuiChanges(guiData.get(direction));
        }

        // TODO: Soon we will swap to records which will mean this will be a new instance.
        //       This API has been designed with this pending change in mind.
        return this;
    }

    public ItemSidedData get(Direction direction) {
        return Objects.requireNonNull(itemSidedData.getOrDefault(direction, ItemSidedData.EMPTY));
    }

    public ItemSidedData compute(Direction direction) {
        return itemSidedData.computeIfAbsent(direction, dir -> new ItemSidedData());
    }

    @Override
    public int hashCode() {
        return itemSidedData.hashCode();
    }

    @Override
    public ConduitDataType<ItemConduitData> type() {
        return ConduitTypes.Data.ITEM.get();
    }

    @Override
    public ItemConduitData deepCopy() {
        var newSidedData = new HashMap<Direction, ItemSidedData>(Direction.values().length);
        for (Direction direction : Direction.values()) {
            if (itemSidedData.containsKey(direction)) {
                newSidedData.put(direction, itemSidedData.get(direction).deepCopy());
            }
        }

        return new ItemConduitData(newSidedData);
    }

    public static class ItemSidedData {

        public static Codec<ItemSidedData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.BOOL.fieldOf("is_round_robin").forGetter(i -> i.isRoundRobin),
                Codec.INT.fieldOf("rotating_index").forGetter(i -> i.rotatingIndex),
                Codec.BOOL.fieldOf("is_self_feed").forGetter(i -> i.isSelfFeed),
                Codec.INT.fieldOf("priority").forGetter(i -> i.priority)
            ).apply(instance, ItemSidedData::new)
        );

        public static StreamCodec<ByteBuf, ItemSidedData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            i -> i.isRoundRobin,
            ByteBufCodecs.BOOL,
            i -> i.isSelfFeed,
            ByteBufCodecs.INT,
            i -> i.priority,
            ItemSidedData::new
        );

        public static ItemSidedData EMPTY = new ItemSidedData(false, 0, false, 0);

        public boolean isRoundRobin = false;
        public int rotatingIndex = 0;
        public boolean isSelfFeed = false;
        public int priority = 0;

        public ItemSidedData() {
        }

        public ItemSidedData(boolean isRoundRobin, boolean isSelfFeed, int priority) {
            this.isRoundRobin = isRoundRobin;
            this.isSelfFeed = isSelfFeed;
            this.priority = priority;
        }

        public ItemSidedData(boolean isRoundRobin, int rotatingIndex, boolean isSelfFeed, int priority) {
            this.isRoundRobin = isRoundRobin;
            this.rotatingIndex = rotatingIndex;
            this.isSelfFeed = isSelfFeed;
            this.priority = priority;
        }

        private void applyGuiChanges(ItemSidedData guiChanges) {
            this.isRoundRobin = guiChanges.isRoundRobin;
            this.isSelfFeed = guiChanges.isSelfFeed;
            this.priority = guiChanges.priority;
        }

        public ItemSidedData deepCopy() {
            return new ItemSidedData(isRoundRobin, rotatingIndex, isSelfFeed, priority);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isRoundRobin, isSelfFeed, priority);
        }
    }
}
