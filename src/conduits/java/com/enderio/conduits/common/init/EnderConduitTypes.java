package com.enderio.conduits.common.init;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EnderConduitTypes {

    public static final RegistryObject<IConduitType> POWER = ConduitTypes.CONDUIT_TYPES.register("power", () -> ConduitType.POWER);

    public static void register() {}
}
