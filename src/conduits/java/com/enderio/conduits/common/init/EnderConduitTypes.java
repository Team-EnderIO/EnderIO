package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitType;
import com.enderio.conduits.common.conduit.type.item.ItemConduitTicker;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitType;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

public class EnderConduitTypes {

    public static final ResourceLocation ICON_TEXTURE = EnderIO.loc("textures/gui/conduit_icon.png");
    public static final RegistryObject<? extends ConduitType<?>> ENERGY = ConduitTypes.CONDUIT_TYPES.register(
        "energy_conduit", EnergyConduitType::new);

    public static final RegistryObject<FluidConduitType> FLUID = fluidConduit("fluid_conduit", 50, false, new Vector2i(0, 120));

    public static final RegistryObject<FluidConduitType> FLUID2 = fluidConduit("pressurized_fluid_conduit", 100, false, new Vector2i(0, 144));

    public static final RegistryObject<FluidConduitType> FLUID3 = fluidConduit("ender_fluid_conduit", 200, true, new Vector2i(0, 168));


    public static final RegistryObject<? extends ConduitType<?>> REDSTONE = ConduitTypes.CONDUIT_TYPES.register("redstone_conduit", RedstoneConduitType::new);
    public static final RegistryObject<? extends ConduitType<?>> ITEM = ConduitTypes.CONDUIT_TYPES.register("item_conduit",
        () -> new SimpleConduitType<>(EnderIO.loc("block/conduit/item"), new ItemConduitTicker(), ItemConduitData::new, new ItemClientConduitData(),
            ConduitMenuData.ITEM));

    private static RegistryObject<FluidConduitType> fluidConduit(String name, int tier, boolean isMultiFluid, Vector2i iconPos) {
        return ConduitTypes.CONDUIT_TYPES.register(name,
            () -> new FluidConduitType(EnderIO.loc("block/conduit/" + name), tier, isMultiFluid, ICON_TEXTURE, iconPos));
    }

    public static void register() {}
}
