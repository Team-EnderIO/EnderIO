//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.EnderIO;
//import com.enderio.api.conduit.ConduitApi;
//import com.enderio.api.conduit.ConduitTypes;
//import com.enderio.api.conduit.IConduitType;
//import com.enderio.api.integration.Integration;
//import com.enderio.api.misc.Vector2i;
//import com.enderio.base.common.init.EIOCreativeTabs;
//import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
//import com.enderio.conduits.common.init.ConduitBlockEntities;
//import com.enderio.regilite.holder.RegiliteItem;
//import com.enderio.regilite.registry.ItemRegistry;
//import mekanism.api.MekanismAPI;
//import mekanism.api.chemical.gas.IGasHandler;
//import mekanism.api.chemical.infuse.IInfusionHandler;
//import mekanism.api.chemical.pigment.IPigmentHandler;
//import mekanism.api.chemical.slurry.ISlurryHandler;
//import mekanism.api.heat.IHeatHandler;
//import net.minecraft.core.Direction;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Item;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.neoforge.capabilities.BlockCapability;
//import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//import net.neoforged.neoforge.registries.DeferredHolder;
//
//import java.util.function.Supplier;
//
//public class MekanismIntegration implements Integration {
//
//    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();
//    private static final DeferredHolder<IConduitType<?>, ChemicalConduitType> CHEMICAL = chemicalConduit("chemical", 750, false, new Vector2i(0,192));
//    private static final DeferredHolder<IConduitType<?>, ChemicalConduitType> CHEMICAL2 = chemicalConduit("pressurized_chemical", 2000, true, new Vector2i(0,216));
//    private static final DeferredHolder<IConduitType<?>, ChemicalConduitType> CHEMICAL3 = chemicalConduit("ender_chemical", 64000, true, new Vector2i(24,0));
//
//    private static final DeferredHolder<IConduitType<?>, HeatConduitType> HEAT_TYPE = heatConduit("heat", new Vector2i(24,24));
//
//    public static final RegiliteItem<Item> CHEMICAL_ITEM = createConduitItem(CHEMICAL, "chemical", "Chemical Conduit");
//    public static final RegiliteItem<Item> PRESSURIZED_CHEMICAL_ITEM = createConduitItem(CHEMICAL2, "pressurized_chemical", "Pressurized Chemical Conduit");
//    public static final RegiliteItem<Item> ENDER_CHEMICAL_ITEM = createConduitItem(CHEMICAL3, "ender_chemical", "Ender Chemical Conduit");
//
//    public static final RegiliteItem<Item> HEAT_ITEM = createConduitItem(HEAT_TYPE, "heat", "Heat Conduit");
//
//    public static final BlockCapability<IGasHandler, Direction> GAS = BlockCapability.createSided(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gas_handler"), IGasHandler.class);
//    public static final BlockCapability<ISlurryHandler, Direction> SLURRY = BlockCapability.createSided(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "slurry_handler"), ISlurryHandler.class);
//    public static final BlockCapability<IInfusionHandler, Direction> INFUSION = BlockCapability.createSided(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "infusion_handler"), IInfusionHandler.class);
//    public static final BlockCapability<IPigmentHandler, Direction> PIGMENT = BlockCapability.createSided(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pigment_handler"), IPigmentHandler.class);
//    public static final BlockCapability<IHeatHandler, Direction> HEAT = BlockCapability.createSided(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "heat_handler"), IHeatHandler.class);
//
//    @Override
//    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
//        ITEM_REGISTRY.register(modEventBus);
//        modEventBus.addListener(this::addCapability);
//    }
//
//    private static DeferredHolder<IConduitType<?>, ChemicalConduitType> chemicalConduit(String name, int tier, boolean isMultiFluid, Vector2i iconPos) {
//        return ConduitTypes.CONDUIT_TYPES.register(name,
//            () -> new ChemicalConduitType(EnderIO.loc("block/conduit/" + name), tier, isMultiFluid, iconPos));
//    }
//
//    private static DeferredHolder<IConduitType<?>, HeatConduitType> heatConduit(String name, Vector2i iconPos) {
//        return ConduitTypes.CONDUIT_TYPES.register(name,
//            () -> new HeatConduitType(EnderIO.loc("block/conduit/" + name), iconPos));
//    }
//
//    private static RegiliteItem<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName, String english) {
//        return ITEM_REGISTRY
//            .registerItem(itemName + "_conduit",
//                properties -> ConduitApi.INSTANCE.createConduitItem(type, properties))
//            .setTab(EIOCreativeTabs.CONDUITS)
//            .setTranslation(english)
//            .setModelProvider((prov, ctx) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()));
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
//}
