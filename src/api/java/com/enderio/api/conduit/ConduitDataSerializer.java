package com.enderio.api.conduit;

import com.mojang.serialization.MapCodec;

public interface ConduitDataSerializer<T extends ExtendedConduitData<T>> {
    MapCodec<T> codec();
}
