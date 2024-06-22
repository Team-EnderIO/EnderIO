package com.enderio.conduits.common;

import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.init.EIOConduitTypes;
import net.minecraftforge.registries.IForgeRegistry;

public class ConduitApiImpl implements ConduitApi {
    @Override
    public IForgeRegistry<ConduitType<?>> getConduitTypeRegistry() {
        return EIOConduitTypes.REGISTRY.get();
    }
}
