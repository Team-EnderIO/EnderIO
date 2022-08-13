package com.enderio.conduits.common.init;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitType;
import net.minecraft.Util;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnderConduitTypes {

    public static final Map<IConduitType, RegistryObject<IConduitType>> ENUM_TYPES = Util.make(() -> {
        HashMap<IConduitType, RegistryObject<IConduitType>> types = new HashMap<>();
        for (ConduitType type : ConduitType.values()) {
            types.put(type, ConduitTypes.CONDUIT_TYPES.register(type.name().toLowerCase(Locale.ROOT), () -> type));
        }
        return types;
    });

    public static void register() {}
}
