package com.enderio.conduits.api;

import net.minecraft.nbt.CompoundTag;

public interface ConduitNetworkContextSerializer<T extends ConduitNetworkContext<T>> {
    CompoundTag save(T context);
    T load(CompoundTag tag);
}
