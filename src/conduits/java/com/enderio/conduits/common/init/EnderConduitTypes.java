package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.RegisteredConduitType;
import com.enderio.conduits.common.blockentity.TieredConduit;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

public class EnderConduitTypes {

    public static final RegistryObject<IConduitType> POWER = powerConduit(1);
    public static final RegistryObject<IConduitType> POWER2 = powerConduit(2);
    public static final RegistryObject<IConduitType> POWER3 = powerConduit(3);

    public static final RegistryObject<IConduitType> REDSTONE = ConduitTypes.CONDUIT_TYPES.register("redstone", () -> new RegisteredConduitType(EnderIO.loc("block/conduit/redstone")));

    private static RegistryObject<IConduitType> powerConduit(int tier) {
        return ConduitTypes.CONDUIT_TYPES.register("power" + tier, () -> new TieredConduit(EnderIO.loc("block/conduit/power" + tier), new ResourceLocation("forge", "power"), tier, null));
    }

    public static void register() {}
}
