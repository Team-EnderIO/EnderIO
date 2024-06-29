package com.enderio.api.conduit;

public interface ConduitNetworkContext<T extends ConduitNetworkContext<T>> {
    T mergeWith(T other);

    T splitFor(ConduitNetwork<T, ?> selfGraph, ConduitNetwork<T, ?> otherGraph);

    Dummy DUMMY = new Dummy();

    record Dummy() implements ConduitNetworkContext<Dummy> {

        @Override
        public Dummy mergeWith(Dummy other) {
            return this;
        }

        @Override
        public Dummy splitFor(ConduitNetwork<Dummy, ?> selfGraph, ConduitNetwork<Dummy, ?> otherGraph) {
            return this;
        }
    }
}
