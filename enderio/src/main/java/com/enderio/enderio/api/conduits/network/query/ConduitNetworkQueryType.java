package com.enderio.enderio.api.conduits.network.query;

import net.minecraft.resources.ResourceKey;

import java.util.Set;
import java.util.function.Supplier;

public record ConduitNetworkQueryType<T extends ConduitNetworkQuery>(Supplier<T> factory, Set<ResourceKey<ConduitNetworkQueryType<?>>> dependentQueries) {}
