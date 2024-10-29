package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MekanismIntegration implements Integration {

    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.Keys.CONDUIT_TYPES, EnderIO.MODID);

    public final Capability<IGasHandler> GAS_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public final Capability<ISlurryHandler> SLURRY_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public final Capability<IInfusionHandler> INFUSION_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public final Capability<IPigmentHandler> PIGMENT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public final Capability<IHeatHandler> HEAT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    private static final RegistryObject<ChemicalConduitType> CHEMICAL = chemicalConduit("chemical", 750, false);
    private static final RegistryObject<ChemicalConduitType> CHEMICAL2 = chemicalConduit("pressurized_chemical", 2_000, true);
    private static final RegistryObject<ChemicalConduitType> CHEMICAL3 = chemicalConduit("ender_chemical", 64_000, true);

    private static final RegistryObject<HeatConduitType> HEAT_TYPE = heatConduit("heat");

    public static final ItemEntry<Item> CHEMICAL_ITEM = createConduitItem(CHEMICAL, "chemical", "Chemical Conduit");
    public static final ItemEntry<Item> PRESSURIZED_CHEMICAL_ITEM = createConduitItem(CHEMICAL2, "pressurized_chemical", "Pressurized Chemical Conduit");
    public static final ItemEntry<Item> ENDER_CHEMICAL_ITEM = createConduitItem(CHEMICAL3, "ender_chemical", "Ender Chemical Conduit");

    public static final ItemEntry<Item> HEAT_ITEM = createConduitItem(HEAT_TYPE, "heat", "Heat Conduit");

    public static final Component LANG_MULTI_CHEMICAL_TOOLTIP = EnderIO.registrate().addLang("item", EnderIO.loc("conduit.chemical.multi"),
        "Allows multiple chemical types to be transported on the same line");

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        CONDUIT_TYPES.register(modEventBus);
    }

    private static RegistryObject<ChemicalConduitType> chemicalConduit(String name, int tier, boolean isMultiFluid) {
        return CONDUIT_TYPES.register(name,
            () -> new ChemicalConduitType(EnderIO.loc(name + "_conduit"), tier, isMultiFluid));
    }

    private static RegistryObject<HeatConduitType> heatConduit(String name) {
        return CONDUIT_TYPES.register(name, HeatConduitType::new);
    }

    private static ItemEntry<Item> createConduitItem(Supplier<? extends ConduitType<?>> type, String itemName, String english) {
        return EnderIO.registrate().item(itemName + "_conduit",
                properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .lang(english)
            .model((ctx, prov) -> {
                var conduitTypeKey = ConduitType.getKey(type.get());
                prov
                    .withExistingParent(ctx.getName(), EnderIO.loc("item/conduit"))
                    .texture("0", EnderIO.loc("block/conduit/" + conduitTypeKey.getPath()));
            })
            .register();
    }
}
