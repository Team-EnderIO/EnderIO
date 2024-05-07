package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.types.EnergyConduitType;
import com.enderio.conduits.common.types.FluidConduitType;
import com.enderio.conduits.common.types.ItemClientConduitData;
import com.enderio.conduits.common.types.ItemConduitTicker;
import com.enderio.conduits.common.types.ItemExtendedData;
import com.enderio.conduits.common.types.RedstoneConduitType;
import com.enderio.conduits.common.types.SimpleConduitType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EnderConduitTypes {

    public static final ResourceLocation ICON_TEXTURE = EnderIO.loc("textures/gui/conduit_icon.png");
    public static final DeferredHolder<ConduitType<?>, EnergyConduitType> ENERGY =
        ConduitTypes.CONDUIT_TYPES.register("energy_conduit", EnergyConduitType::new);

    public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID = fluidConduit("fluid_conduit", 50, false, new Vector2i(0, 120));

    public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID2 = fluidConduit("pressurized_fluid_conduit", 100, false, new Vector2i(0, 144));

    public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID3 = fluidConduit("ender_fluid_conduit", 200, true, new Vector2i(0, 168));


    public static final DeferredHolder<ConduitType<?>, RedstoneConduitType> REDSTONE = ConduitTypes.CONDUIT_TYPES.register("redstone_conduit", RedstoneConduitType::new);
    public static final DeferredHolder<ConduitType<?>, SimpleConduitType<ItemExtendedData>> ITEM = ConduitTypes.CONDUIT_TYPES.register("item_conduit",
        () -> new SimpleConduitType<>(EnderIO.loc("block/conduit/item"), new ItemConduitTicker(), ItemExtendedData::new, new ItemClientConduitData(),
            ConduitMenuData.ITEM));

    private static DeferredHolder<ConduitType<?>, FluidConduitType> fluidConduit(String name, int tier, boolean isMultiFluid, Vector2i iconPos) {
        return ConduitTypes.CONDUIT_TYPES.register(name,
            () -> new FluidConduitType(EnderIO.loc("block/conduit/" + name), tier, isMultiFluid, ICON_TEXTURE, iconPos));
    }

    public static void register() {}
}
