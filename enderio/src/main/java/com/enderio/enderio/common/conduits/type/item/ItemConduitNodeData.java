package com.enderio.enderio.common.conduits.type.item;

import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.enderio.conduits.common.init.ConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stores round-robin indexes for this node.
 */
public final class ItemConduitNodeData implements NodeData {

    public static final MapCodec<ItemConduitNodeData> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(Codec.unboundedMap(Direction.CODEC, Codec.INT)
                    .fieldOf("round_robin_indexes")
                    .forGetter(i -> i.roundRobinIndexes)).apply(instance, ItemConduitNodeData::new));

    public static final NodeDataType<ItemConduitNodeData> TYPE = new NodeDataType<>(ItemConduitNodeData.CODEC,
            ItemConduitNodeData::new);
    private final Map<Direction, Integer> roundRobinIndexes;

    public ItemConduitNodeData() {
        this(Map.of());
    }

    public ItemConduitNodeData(Map<Direction, Integer> roundRobinIndexes) {
        this.roundRobinIndexes = new HashMap<>(roundRobinIndexes);
    }

    @Override
    public NodeDataType<?> type() {
        return ConduitTypes.NodeData.ITEM.get();
    }

    public int getIndex(Direction side) {
        return roundRobinIndexes.getOrDefault(side, 0);
    }

    public void setIndex(Direction side, int index) {
        roundRobinIndexes.put(side, index);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemConduitNodeData other)) {
            return false;
        }
        return Objects.equals(roundRobinIndexes, other.roundRobinIndexes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roundRobinIndexes);
    }
}
