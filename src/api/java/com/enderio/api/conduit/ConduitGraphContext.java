package com.enderio.api.conduit;

public interface ConduitGraphContext<T extends ConduitGraphContext<T>> {
    T mergeWith(T other);

    T splitFor(ConduitGraph<T, ?> selfGraph, ConduitGraph<T, ?> otherGraph);

    Dummy DUMMY = new Dummy();

    record Dummy() implements ConduitGraphContext<Dummy> {

        @Override
        public Dummy mergeWith(Dummy other) {
            return this;
        }

        @Override
        public Dummy splitFor(ConduitGraph<Dummy, ?> selfGraph, ConduitGraph<Dummy, ?> otherGraph) {
            return this;
        }
    }
}
