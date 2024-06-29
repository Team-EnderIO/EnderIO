package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MekanismIntegration implements Integration {

    public static final DeferredRegister<ConduitNetworkType<?, ?, ?>> CONDUIT_NETWORK_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_NETWORK_TYPES, EnderIO.MODID);
    public static final DeferredRegister<ConduitType<?, ?, ?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPES, EnderIO.MODID);
    public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZERS, EnderIO.MODID);

    private static final Supplier<ChemicalConduitNetworkType> CHEMICAL_NETWORK_TYPE = CONDUIT_NETWORK_TYPES.register("chemical", ChemicalConduitNetworkType::new);
    private static final Supplier<HeatConduitNetworkType> HEAT_NETWORK_TYPE = CONDUIT_NETWORK_TYPES.register("heat", HeatConduitNetworkType::new);

    public static final Supplier<ConduitType<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData>> CHEMICAL =
        register("chemical", CHEMICAL_NETWORK_TYPE, new ChemicalConduitOptions(750, false));
    public static final Supplier<ConduitType<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData>> CHEMICAL2 =
        register("pressurized_chemical", CHEMICAL_NETWORK_TYPE, new ChemicalConduitOptions(2000, true));
    public static final Supplier<ConduitType<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData>> CHEMICAL3 =
        register("ender_chemical", CHEMICAL_NETWORK_TYPE, new ChemicalConduitOptions(64000, true));

    public static final Supplier<ConduitDataSerializer<ChemicalConduitData>> CHEMICAL_DATA_SERIALIZER =
        CONDUIT_DATA_SERIALIZERS.register("chemical", ChemicalConduitData.Serializer::new);

    public static final Supplier<ConduitType<Void, ConduitNetworkContext.Dummy, ConduitData.EmptyConduitData>> HEAT_TYPE =
        register("heat", HEAT_NETWORK_TYPE, null);

    public static final BlockCapability<IGasHandler, Direction> GAS = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "gas_handler"), IGasHandler.class);
    public static final BlockCapability<ISlurryHandler, Direction> SLURRY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "slurry_handler"), ISlurryHandler.class);
    public static final BlockCapability<IInfusionHandler, Direction> INFUSION = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "infusion_handler"), IInfusionHandler.class);
    public static final BlockCapability<IPigmentHandler, Direction> PIGMENT = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "pigment_handler"), IPigmentHandler.class);
    public static final BlockCapability<IHeatHandler, Direction> HEAT = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "heat_handler"), IHeatHandler.class);

    private static final Component LANG_HEAT_CONDUIT = addTranslation("item", EnderIO.loc("conduit.heat"), "Heat Conduit");
    private static final Component LANG_CHEMICAL_CONDUIT = addTranslation("item", EnderIO.loc("conduit.chemical"), "Chemical Conduit");
    private static final Component LANG_PRESSURIZED_CHEMICAL_CONDUIT = addTranslation("item", EnderIO.loc("conduit.pressurized_chemical"), "Pressurized Chemical");
    private static final Component LANG_ENDER_CHEMICAL_CONDUIT = addTranslation("item", EnderIO.loc("conduit.ender_chemical"), "Ender Chemical");

    public static final Component LANG_MULTI_CHEMICAL_TOOLTIP = addTranslation("item", EnderIO.loc("conduit.chemical.multi"),
        "Allows multiple chemical types to be transported on the same line");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.getRegilite().addTranslation(prefix, id, translation);
    }

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        CONDUIT_NETWORK_TYPES.register(modEventBus);
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_DATA_SERIALIZERS.register(modEventBus);
    }

    private static <T, U extends ConduitNetworkContext<U>, V extends ConduitData<V>, W extends ConduitNetworkType<T, U, V>> DeferredHolder<ConduitType<?, ?, ?>, ConduitType<T, U, V>> register(String name,
        Supplier<W> graphType, T options) {
        return CONDUIT_TYPES.register(name, () -> new ConduitType<>(graphType.get(), options));
    }
}
