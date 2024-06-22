package com.enderio.api.conduit;

import net.minecraftforge.registries.IForgeRegistry;

import java.util.ServiceLoader;

public interface ConduitApi {

    ConduitApi INSTANCE = ServiceLoader.load(ConduitApi.class).findFirst().orElseThrow();

    IForgeRegistry<ConduitType<?>> getConduitTypeRegistry();
}
