package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.integration.Integration;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MekanismIntegration implements Integration {

//    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();
//    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPES, EnderIO.MODID);
//    public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZERS, EnderIO.MODID);
//
//    private static final DeferredHolder<ConduitType<?>, ChemicalConduitType> CHEMICAL = chemicalConduit("chemical", 750, false);
//    private static final DeferredHolder<ConduitType<?>, ChemicalConduitType> CHEMICAL2 = chemicalConduit("pressurized_chemical", 2000, true);
//    private static final DeferredHolder<ConduitType<?>, ChemicalConduitType> CHEMICAL3 = chemicalConduit("ender_chemical", 64000, true);
//
//    public static final Supplier<ConduitDataSerializer<ChemicalConduitData>> CHEMICAL_DATA_SERIALIZER =
//        CONDUIT_DATA_SERIALIZERS.register("chemical", ChemicalConduitData.Serializer::new);
//
//    private static final DeferredHolder<ConduitType<?>, HeatConduitType> HEAT_TYPE = heatConduit("heat");
//
//    public static final RegiliteItem<Item> CHEMICAL_ITEM = createConduitItem(CHEMICAL, "chemical", "Chemical Conduit");
//    public static final RegiliteItem<Item> PRESSURIZED_CHEMICAL_ITEM = createConduitItem(CHEMICAL2, "pressurized_chemical", "Pressurized Chemical Conduit");
//    public static final RegiliteItem<Item> ENDER_CHEMICAL_ITEM = createConduitItem(CHEMICAL3, "ender_chemical", "Ender Chemical Conduit");
//
//    public static final RegiliteItem<Item> HEAT_ITEM = createConduitItem(HEAT_TYPE, "heat", "Heat Conduit");
//
//    public static final BlockCapability<IGasHandler, Direction> GAS = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "gas_handler"), IGasHandler.class);
//    public static final BlockCapability<ISlurryHandler, Direction> SLURRY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "slurry_handler"), ISlurryHandler.class);
//    public static final BlockCapability<IInfusionHandler, Direction> INFUSION = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "infusion_handler"), IInfusionHandler.class);
//    public static final BlockCapability<IPigmentHandler, Direction> PIGMENT = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "pigment_handler"), IPigmentHandler.class);
//    public static final BlockCapability<IHeatHandler, Direction> HEAT = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "heat_handler"), IHeatHandler.class);
//
//    @Override
//    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
//        ITEM_REGISTRY.register(modEventBus);
//        CONDUIT_TYPES.register(modEventBus);
//        CONDUIT_DATA_SERIALIZERS.register(modEventBus);
//        modEventBus.addListener(this::addCapability);
//    }
//
//    private static DeferredHolder<ConduitType<?>, ChemicalConduitType> chemicalConduit(String name, int tier, boolean isMultiFluid) {
//        return CONDUIT_TYPES.register(name,
//            () -> new ChemicalConduitType(EnderIO.loc(name + "_conduit"), tier, isMultiFluid));
//    }
//
//    private static DeferredHolder<ConduitType<?>, HeatConduitType> heatConduit(String name) {
//        return CONDUIT_TYPES.register(name, HeatConduitType::new);
//    }
//
//    private static RegiliteItem<Item> createConduitItem(Supplier<? extends ConduitType<?>> type, String itemName, String english) {
//        return ITEM_REGISTRY
//            .registerItem(itemName + "_conduit",
//                properties -> ConduitApi.INSTANCE.createConduitItem(type, properties))
//            .setTab(EIOCreativeTabs.CONDUITS)
//            .setTranslation(english)
//            .setModelProvider((prov, ctx) -> {
//                var conduitTypeKey = ConduitType.getKey(type.get());
//                prov
//                    .withExistingParent(conduitTypeKey.getPath() + "_conduit", EnderIO.loc("item/conduit"))
//                    .texture("0", EnderIO.loc("block/conduit/" + conduitTypeKey.getPath()));});
//    }
//
//    public void addCapability(RegisterCapabilitiesEvent event) {
//        event.registerBlockEntity(MekanismIntegration.GAS, ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(MekanismIntegration.GAS));
//        event.registerBlockEntity(MekanismIntegration.SLURRY, ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(MekanismIntegration.SLURRY));
//        event.registerBlockEntity(MekanismIntegration.INFUSION, ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(MekanismIntegration.INFUSION));
//        event.registerBlockEntity(MekanismIntegration.PIGMENT, ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(MekanismIntegration.PIGMENT));
//        event.registerBlockEntity(MekanismIntegration.HEAT, ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(MekanismIntegration.HEAT));
//
//    }
}
