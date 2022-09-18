package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.conduits.common.blockentity.PowerConduitType;
import com.enderio.conduits.common.blockentity.SimpleConduitType;
import com.enderio.conduits.common.network.ItemConduitTicker;
import com.enderio.conduits.common.network.RedstoneConduitTicker;
import com.enderio.conduits.common.network.RedstoneConduitType;
import com.enderio.conduits.common.network.RedstoneExtraData;
import net.minecraftforge.registries.RegistryObject;

public class EnderConduitTypes {

    public static final RegistryObject<PowerConduitType> POWER = powerConduit(1);
    public static final RegistryObject<PowerConduitType> POWER2 = powerConduit(2);
    public static final RegistryObject<PowerConduitType> POWER3 = powerConduit(3);

    public static final RegistryObject<? extends IConduitType<?>> REDSTONE = ConduitTypes.CONDUIT_TYPES.register("redstone", RedstoneConduitType::new);
    public static final RegistryObject<? extends IConduitType<?>> ITEM = ConduitTypes.CONDUIT_TYPES.register("item", () -> new SimpleConduitType<>(EnderIO.loc("block/conduit/item"),
        new ItemConduitTicker(), IExtendedConduitData.dummy()));

    private static RegistryObject<PowerConduitType> powerConduit(int tier) {
        return ConduitTypes.CONDUIT_TYPES.register("power" + tier, () -> new PowerConduitType(EnderIO.loc("block/conduit/power" + tier), tier, null));
    }

    public static void register() {}
}
