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
    public static final RegistryObject<PowerConduitType> POWER = powerConduit(1, 1280, new Vector2i(0,24));
    public static final RegistryObject<PowerConduitType> POWER2 = powerConduit(2, 5120, new Vector2i(0,48));
    public static final RegistryObject<PowerConduitType> POWER3 = powerConduit(3, 20480, new Vector2i(0,72));
    public static final RegistryObject<FluidConduitType> FLUID = fluidConduit("fluid_conduit", 50, false, new Vector2i(0,144));
    public static final RegistryObject<FluidConduitType> FLUID2 = fluidConduit("pressurized_fluid_conduit", 100,false, new Vector2i(0,168));
    public static final RegistryObject<FluidConduitType> FLUID3 = fluidConduit("ender_fluid_conduit",  200, true, new Vector2i(0,192));

    public static final RegistryObject<? extends IConduitType<?>> REDSTONE = ConduitTypes.CONDUIT_TYPES.register("redstone_conduit", RedstoneConduitType::new);
    public static final RegistryObject<? extends IConduitType<?>> ITEM = ConduitTypes.CONDUIT_TYPES.register("item_conduit",
        () -> new SimpleConduitType<>(EnderIO.loc("block/conduit/item"), new ItemConduitTicker(), ItemExtendedData::new, new ItemClientConduitData(),
            IConduitMenuData.ITEM));

    private static RegistryObject<PowerConduitType> powerConduit(int namedtier, int tier, Vector2i iconPos) {
        return ConduitTypes.CONDUIT_TYPES.register("power" + namedtier + "_conduit", () -> new PowerConduitType(EnderIO.loc("block/conduit/power" + namedtier), tier, ICON_TEXTURE, iconPos));
    }
    private static RegistryObject<FluidConduitType> fluidConduit(String name, int tier, boolean isMultiFluid, Vector2i iconPos) {
        return ConduitTypes.CONDUIT_TYPES.register(name, () -> new FluidConduitType(EnderIO.loc("block/conduit/" + name), tier, isMultiFluid, ICON_TEXTURE, iconPos));
    }

    public static void register() {}
}
