//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.EnderIO;
//import com.enderio.api.conduit.Conduit;
//import com.enderio.api.conduit.ConduitDataSerializer;
//import com.enderio.api.conduit.ConduitType;
//import com.enderio.api.integration.Integration;
//import com.enderio.api.registry.EnderIORegistries;
//import mekanism.api.MekanismAPI;
//import mekanism.api.chemical.gas.IGasHandler;
//import mekanism.api.chemical.infuse.IInfusionHandler;
//import mekanism.api.chemical.pigment.IPigmentHandler;
//import mekanism.api.chemical.slurry.ISlurryHandler;
//import mekanism.api.heat.IHeatHandler;
//import net.minecraft.core.Direction;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.MutableComponent;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.neoforge.capabilities.BlockCapability;
//import net.neoforged.neoforge.registries.DeferredRegister;
//
//import java.util.function.Supplier;
//
//public class MekanismIntegration implements Integration {
//
//
//    public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZER, EnderIO.MODID);
//
//    public static class Types {
//
//        private static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPE, EnderIO.MODID);
//
//        public static final Supplier<ConduitType<ChemicalConduit>> CHEMICAL = CONDUIT_TYPES
//            .register("chemical", () -> ConduitType.of(ChemicalConduit.CODEC));
//
//        public static final Supplier<ConduitType<HeatConduit>> HEAT = CONDUIT_TYPES
//            .register("heat", () -> ConduitType.of(HeatConduit::new));
//    }
//
//    public static class Capabilities {
//        public static final BlockCapability<IGasHandler, Direction> GAS = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "gas_handler"), IGasHandler.class);
//        public static final BlockCapability<ISlurryHandler, Direction> SLURRY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "slurry_handler"), ISlurryHandler.class);
//        public static final BlockCapability<IInfusionHandler, Direction> INFUSION = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "infusion_handler"), IInfusionHandler.class);
//        public static final BlockCapability<IPigmentHandler, Direction> PIGMENT = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "pigment_handler"), IPigmentHandler.class);
//        public static final BlockCapability<IHeatHandler, Direction> HEAT = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "heat_handler"), IHeatHandler.class);
//    }
//
//    public static final ResourceKey<Conduit<?>> CHEMICAL = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("chemical"));
//    public static final ResourceKey<Conduit<?>> PRESSURIZED_CHEMICAL = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("pressurized_chemical"));
//    public static final ResourceKey<Conduit<?>> ENDER_CHEMICAL = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("ender_chemical"));
//    public static final ResourceKey<Conduit<?>> HEAT = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("heat"));
//
//    public static final Supplier<ConduitDataSerializer<ChemicalConduitData>> CHEMICAL_DATA_SERIALIZER =
//        CONDUIT_DATA_SERIALIZERS.register("chemical", ChemicalConduitData.Serializer::new);
//
//    private static final Component LANG_HEAT_CONDUIT = addTranslation("item", EnderIO.loc("conduit.heat"), "Heat Conduit");
//    private static final Component LANG_CHEMICAL_CONDUIT = addTranslation("item", EnderIO.loc("conduit.chemical"), "Chemical Conduit");
//    private static final Component LANG_PRESSURIZED_CHEMICAL_CONDUIT = addTranslation("item", EnderIO.loc("conduit.pressurized_chemical"), "Pressurized Chemical Conduit");
//    private static final Component LANG_ENDER_CHEMICAL_CONDUIT = addTranslation("item", EnderIO.loc("conduit.ender_chemical"), "Ender Chemical Conduit");
//
//    public static final Component LANG_MULTI_CHEMICAL_TOOLTIP = addTranslation("item", EnderIO.loc("conduit.chemical.multi"),
//        "Allows multiple chemical types to be transported on the same line");
//
//    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
//        return EIOConduits.REGILITE.addTranslation(prefix, id, translation);
//    }
//
//    @Override
//    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
//        Types.CONDUIT_TYPES.register(modEventBus);
//        CONDUIT_DATA_SERIALIZERS.register(modEventBus);
//    }
//}
