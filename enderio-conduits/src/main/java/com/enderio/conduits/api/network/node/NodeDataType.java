package com.enderio.conduits.api.network.node;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import org.jetbrains.annotations.Nullable;

public final class NodeDataType<T extends NodeData> {
    private final boolean isPersistent;
    private final MapCodec<T> codec;
    private final Supplier<T> factory;

    /**
     * Creates a new node data type.
     * @param codec The codec used to serialize and deserialize the data. Can be null for non-persistent node data.
     * @param factory A factory for creating a new default instance.
     */
    public NodeDataType(@Nullable MapCodec<T> codec, Supplier<T> factory) {
        if (codec != null) {
            this.codec = codec;
            this.isPersistent = true;
        } else {
            // If we're not persistent, create a codec that always decodes to the default instance.
            // This way, upgrading from a time when data was persistent will not fail.
            this.codec = new MapCodec<>() {
                @Override
                public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
                    return Stream.empty();
                }

                @Override
                public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
                    // Return default instance if we're trying to decode for some reason.
                    return DataResult.success(create());
                }

                @Override
                public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
                    // Do not ever encode non-persistent node data...
                    throw new UnsupportedOperationException("Node data cannot be saved - not persistent.");
                }
            };
            this.isPersistent = false;
        }

        this.factory = factory;
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public MapCodec<T> codec() {
        return codec;
    }

    public T create() {
        return factory.get();
    }
}
