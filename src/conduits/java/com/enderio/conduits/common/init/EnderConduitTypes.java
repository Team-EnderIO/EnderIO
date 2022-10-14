package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.api.conduit.PowerConduitType;
import com.enderio.conduits.common.types.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

public class EnderConduitTypes {

    public static final ResourceLocation ICON_TEXTURE = EnderIO.loc("textures/gui/conduit_icon.png");
    public static final RegistryObject<PowerConduitType> POWER = powerConduit(1, new Vector2i(0,24));
    public static final RegistryObject<PowerConduitType> POWER2 = powerConduit(2, new Vector2i(0,48));
    public static final RegistryObject<PowerConduitType> POWER3 = powerConduit(3, new Vector2i(0,72));

    public static final RegistryObject<? extends IConduitType<?>> REDSTONE = ConduitTypes.CONDUIT_TYPES.register("redstone_conduit", RedstoneConduitType::new);
    public static final RegistryObject<? extends IConduitType<?>> ITEM = ConduitTypes.CONDUIT_TYPES.register("item_conduit",
        () -> new SimpleConduitType<>(EnderIO.loc("block/conduit/item"), new ItemConduitTicker(), ItemExtendedData::new, new ItemClientConduitData(),
            IConduitMenuData.ITEM));

    private static RegistryObject<PowerConduitType> powerConduit(int tier, Vector2i iconPos) {
        return ConduitTypes.CONDUIT_TYPES.register("power" + tier + "_conduit", () -> new PowerConduitType(EnderIO.loc("block/conduit/power" + tier), tier, ICON_TEXTURE, iconPos));
    }

    public static void register() {}
}
