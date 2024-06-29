package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

public interface ConduitNetworkContext<T extends ConduitNetworkContext<T>> {
    T mergeWith(T other);

    T copy();

    /**
     * Get the serializer for this context.
     * @apiNote Returning null means this context cannot be serialized.
     */
    @Nullable
    ConduitNetworkContextSerializer<T> serializer();

    Dummy DUMMY = new Dummy();

    record Dummy() implements ConduitNetworkContext<Dummy> {

        @Override
        public Dummy mergeWith(Dummy other) {
            return this;
        }

        @Override
        public Dummy copy() {
            return this;
        }

        @Override
        public @Nullable ConduitNetworkContextSerializer<Dummy> serializer() {
            return null;
        }
    }
}
