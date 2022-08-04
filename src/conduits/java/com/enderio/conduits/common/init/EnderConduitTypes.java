package com.enderio.conduits.common.init;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EnderConduitTypes {

    private static final DeferredRegister<IConduitType> CONDUIT_TYPES = DeferredRegister.create(ConduitTypes.REGISTRY.get(), "enderio");

    public static final RegistryObject<IConduitType> POWER = CONDUIT_TYPES.register("power", () -> ConduitType.POWER);

}
