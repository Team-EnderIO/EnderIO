package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitType;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitData;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemClientConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduitTicker;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitType;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOConduitTypes {

    public static final ResourceLocation ICON_TEXTURE = EnderIO.loc("textures/gui/conduit_icon.png");

    public static class Types {
        public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPES, EnderIO.MODID);

        public static final DeferredHolder<ConduitType<?>, EnergyConduitType> ENERGY =
            CONDUIT_TYPES.register("energy_conduit", EnergyConduitType::new);

        public static final DeferredHolder<ConduitType<?>, RedstoneConduitType> REDSTONE =
            CONDUIT_TYPES.register("redstone_conduit", RedstoneConduitType::new);

        public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID =
            fluidConduit("fluid_conduit", 50, false, new Vector2i(0, 120));

        public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID2 =
            fluidConduit("pressurized_fluid_conduit", 100, false, new Vector2i(0, 144));

        public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID3 =
            fluidConduit("ender_fluid_conduit", 200, true, new Vector2i(0, 168));

        public static final DeferredHolder<ConduitType<?>, SimpleConduitType<ItemConduitData>> ITEM =
            CONDUIT_TYPES.register("item_conduit",
                () -> new SimpleConduitType<>(
                    EnderIO.loc("block/conduit/item"),
                    new ItemConduitTicker(),
                    ItemConduitData::new,
                    new ItemClientConduitData(),
                    ConduitMenuData.ITEM));

        private static DeferredHolder<ConduitType<?>, FluidConduitType> fluidConduit(String name, int tier, boolean isMultiFluid, Vector2i iconPos) {
            return CONDUIT_TYPES.register(name,
                () -> new FluidConduitType(EnderIO.loc("block/conduit/" + name), tier, isMultiFluid, ICON_TEXTURE, iconPos));
        }
    }

    public static class Serializers {
        public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZERS, EnderIO.MODID);

        // Register the API data type.
        public static DeferredHolder<ConduitDataSerializer<?>, ConduitData.EmptyConduitData.Serializer> EMPTY =
            CONDUIT_DATA_SERIALIZERS.register("empty", () -> ConduitData.EmptyConduitData.Serializer.INSTANCE);

        public static final Supplier<ConduitDataSerializer<EnergyConduitData>> ENERGY =
            CONDUIT_DATA_SERIALIZERS.register("energy_conduit", EnergyConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<FluidConduitData>> FLUID =
            CONDUIT_DATA_SERIALIZERS.register("fluid_conduit", FluidConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<RedstoneConduitData>> REDSTONE =
            CONDUIT_DATA_SERIALIZERS.register("redstone_conduit", RedstoneConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<ItemConduitData>> ITEM =
            CONDUIT_DATA_SERIALIZERS.register("item_conduit", ItemConduitData.Serializer::new);
    }

    public static void register(IEventBus bus) {
        Types.CONDUIT_TYPES.register(bus);
        Serializers.CONDUIT_DATA_SERIALIZERS.register(bus);
    }
}
