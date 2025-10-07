package com.enderio.enderio.init;

import com.enderio.core.common.registries.ItemDeferredRegister;
import com.enderio.core.data.model.ModelHelper;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.content.broken_spawner.BrokenSpawnerItem;
import com.enderio.enderio.content.capacitors.CapacitorItem;
import com.enderio.enderio.content.capacitors.LootCapacitorItem;
import com.enderio.enderio.content.cold_fire.ColdFireIgniter;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilterItem;
import com.enderio.enderio.content.filters.item.general.EnderItemFilterItem;
import com.enderio.enderio.content.filters.soul.EnderSoulFilterItem;
import com.enderio.enderio.content.fun.EnderiosItem;
import com.enderio.enderio.content.tools.ElectromagnetItem;
import com.enderio.enderio.content.tools.LevitationStaffItem;
import com.enderio.enderio.content.tools.PoweredToggledItem;
import com.enderio.enderio.content.tools.YetaWrenchItem;
import com.enderio.enderio.content.tools.coordinate_selector.CoordinateSelectorItem;
import com.enderio.enderio.content.tools.coordinate_selector.LocationPrintoutItem;
import com.enderio.enderio.content.tools.hang_glider.HangGliderItem;
import com.enderio.enderio.content.travel.TravelStaffItem;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.content.tools.vials.VoidVialItem;
import com.enderio.enderio.foundation.item.CreativeTabIconItem;
import com.enderio.enderio.foundation.item.LoreItem;
import com.enderio.enderio.foundation.item.MaterialItem;
import com.enderio.enderio.foundation.lang.EIOLang;
import com.enderio.enderio.foundation.soul.SoulCapabilityProviders;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Function;

@SuppressWarnings("unused")
public class EIOItems {
    private static final ItemRegistry ITEM_REGISTRY = EnderIO.REGILITE.itemRegistry();

    private static final ItemDeferredRegister ITEMS = ItemDeferredRegister.create(EnderIO.MOD_ID);

    // ======== NEW ========

    // region Alloys

    public static final DeferredItem<MaterialItem> COPPER_ALLOY_INGOT = material("copper_alloy_ingot");
    public static final DeferredItem<MaterialItem> ENERGETIC_ALLOY_INGOT = material("energetic_alloy_ingot");
    public static final DeferredItem<MaterialItem> VIBRANT_ALLOY_INGOT = material("vibrant_alloy_ingot");
    public static final DeferredItem<MaterialItem> REDSTONE_ALLOY_INGOT = material("redstone_alloy_ingot");
    public static final DeferredItem<MaterialItem> CONDUCTIVE_ALLOY_INGOT = material("conductive_alloy_ingot");
    public static final DeferredItem<MaterialItem> PULSATING_ALLOY_INGOT = material("pulsating_alloy_ingot");
    public static final DeferredItem<MaterialItem> DARK_STEEL_INGOT = material("dark_steel_ingot");
    public static final DeferredItem<MaterialItem> SOULARIUM_INGOT = material("soularium_ingot");
    public static final DeferredItem<MaterialItem> END_STEEL_INGOT = material("end_steel_ingot");

    public static final DeferredItem<MaterialItem> COPPER_ALLOY_NUGGET = material("copper_alloy_nugget");
    public static final DeferredItem<MaterialItem> ENERGETIC_ALLOY_NUGGET = material("energetic_alloy_nugget");
    public static final DeferredItem<MaterialItem> VIBRANT_ALLOY_NUGGET = material("vibrant_alloy_nugget");
    public static final DeferredItem<MaterialItem> REDSTONE_ALLOY_NUGGET = material("redstone_alloy_nugget");
    public static final DeferredItem<MaterialItem> CONDUCTIVE_ALLOY_NUGGET = material("conductive_alloy_nugget");
    public static final DeferredItem<MaterialItem> PULSATING_ALLOY_NUGGET = material("pulsating_alloy_nugget");
    public static final DeferredItem<MaterialItem> DARK_STEEL_NUGGET = material("dark_steel_nugget");
    public static final DeferredItem<MaterialItem> SOULARIUM_NUGGET = material("soularium_nugget");
    public static final DeferredItem<MaterialItem> END_STEEL_NUGGET = material("end_steel_nugget");

    // endregion

    // region Crafting Components

    public static final DeferredItem<MaterialItem> SILICON = material("silicon");
    public static final DeferredItem<MaterialItem> GRAINS_OF_INFINITY = material("grains_of_infinity");
    public static final DeferredItem<MaterialItem> INFINITY_ROD = material("infinity_rod");
    public static final DeferredItem<MaterialItem> CONDUIT_BINDER_COMPOSITE = material("conduit_binder_composite");
    public static final DeferredItem<MaterialItem> CONDUIT_BINDER = material("conduit_binder");
    public static final DeferredItem<MaterialItem> ZOMBIE_ELECTRODE = material("zombie_electrode");
    public static final DeferredItem<MaterialItem> Z_LOGIC_CONTROLLER = material("z_logic_controller");
    public static final DeferredItem<MaterialItem> FRANK_N_ZOMBIE = materialGlinted("frank_n_zombie");
    public static final DeferredItem<MaterialItem> ENDER_RESONATOR = material("ender_resonator");
    public static final DeferredItem<MaterialItem> SENTIENT_ENDER = materialGlinted("sentient_ender");
    public static final DeferredItem<MaterialItem> SKELETAL_CONTRACTOR = material("skeletal_contractor");
    public static final DeferredItem<MaterialItem> GUARDIAN_DIODE = material("guardian_diode");
    public static final DeferredItem<LoreItem> SUSPICIOUS_SEED = lore("suspicious_seed", EIOLang.SUSPICIOUS_SEED_LORE);

    // endregion

    private static DeferredItem<MaterialItem> material(String name) {
        return ITEMS.builder(name, p -> new MaterialItem(p, false))
            .tab(EIOCreativeTabs.MAIN)
            .build();
    }

    private static DeferredItem<MaterialItem> materialGlinted(String name) {
        return ITEMS.builder(name, p -> new MaterialItem(p, true))
            .tab(EIOCreativeTabs.MAIN)
            .build();
    }

    private static DeferredItem<LoreItem> lore(String name, Component lore) {
        return ITEMS.builder(name, p -> new LoreItem(p, true, lore), new Item.Properties().rarity(Rarity.RARE))
            .tab(EIOCreativeTabs.MAIN)
            .build();
    }

    // ======== OLD ========

    // region Capacitors

    public static final RegiliteItem<CapacitorItem> BASIC_CAPACITOR = ITEM_REGISTRY
            .registerItem("basic_capacitor",
                    props -> new CapacitorItem(
                            props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(1))))
            .setTab(EIOCreativeTabs.MAIN);

    public static final RegiliteItem<CapacitorItem> DOUBLE_LAYER_CAPACITOR = ITEM_REGISTRY
            .registerItem("double_layer_capacitor",
                    props -> new CapacitorItem(
                            props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(2))))
            .setTab(EIOCreativeTabs.MAIN);

    public static final RegiliteItem<CapacitorItem> OCTADIC_CAPACITOR = ITEM_REGISTRY
            .registerItem("octadic_capacitor",
                    props -> new CapacitorItem(
                            props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(3))))
            .setTab(EIOCreativeTabs.MAIN);

    public static final RegiliteItem<LootCapacitorItem> LOOT_CAPACITOR = ITEM_REGISTRY.registerItem("loot_capacitor",
            LootCapacitorItem::new, new Item.Properties().stacksTo(1));

    // endregion

    // region Crystals

    public static final RegiliteItem<MaterialItem> PULSATING_CRYSTAL = materialItemGlinted("pulsating_crystal");
    public static final RegiliteItem<MaterialItem> VIBRANT_CRYSTAL = materialItemGlinted("vibrant_crystal");
    public static final RegiliteItem<MaterialItem> ENDER_CRYSTAL = materialItemGlinted("ender_crystal");
    public static final RegiliteItem<MaterialItem> ENTICING_CRYSTAL = materialItemGlinted("enticing_crystal");
    public static final RegiliteItem<MaterialItem> WEATHER_CRYSTAL = materialItemGlinted("weather_crystal");
    public static final RegiliteItem<MaterialItem> PRESCIENT_CRYSTAL = materialItemGlinted("prescient_crystal");

    // endregion

    // region Powders and Fragments

    public static final RegiliteItem<MaterialItem> FLOUR = materialItem("flour");
    public static final RegiliteItem<MaterialItem> POWDERED_COAL = materialItem("powdered_coal");

    public static final RegiliteItem<MaterialItem> POWDERED_IRON = materialItem("powdered_iron");

    public static final RegiliteItem<MaterialItem> POWDERED_GOLD = materialItem("powdered_gold");

    public static final RegiliteItem<MaterialItem> POWDERED_COPPER = materialItem("powdered_copper");

    public static final RegiliteItem<MaterialItem> POWDERED_TIN = materialItem("powdered_tin"); // TODO: hide if tin isn't present

    public static final RegiliteItem<MaterialItem> POWDERED_ENDER_PEARL = materialItem("powdered_ender_pearl");

    public static final RegiliteItem<MaterialItem> POWDERED_OBSIDIAN = materialItem("powdered_obsidian");

    public static final RegiliteItem<MaterialItem> POWDERED_COBALT = materialItem("powdered_cobalt"); // TODO: hide if cobalt isnt present

    public static final RegiliteItem<MaterialItem> POWDERED_LAPIS_LAZULI = materialItem("powdered_lapis_lazuli");

    public static final RegiliteItem<MaterialItem> POWDERED_QUARTZ = materialItem("powdered_quartz");

    public static final RegiliteItem<MaterialItem> PRESCIENT_POWDER = materialItemGlinted("prescient_powder")
            .setTranslation("Grains of Prescience");

    public static final RegiliteItem<MaterialItem> VIBRANT_POWDER = materialItemGlinted("vibrant_powder")
            .setTranslation("Grains of Vibrancy");

    public static final RegiliteItem<MaterialItem> PULSATING_POWDER = materialItemGlinted("pulsating_powder")
            .setTranslation("Grains of Piezallity");

    public static final RegiliteItem<MaterialItem> ENDER_CRYSTAL_POWDER = materialItemGlinted("ender_crystal_powder")
            .setTranslation("Grains of the End");

    public static final RegiliteItem<MaterialItem> PHOTOVOLTAIC_COMPOSITE = materialItem("photovoltaic_composite");
    public static final RegiliteItem<MaterialItem> SOUL_POWDER = materialItem("soul_powder");
    public static final RegiliteItem<MaterialItem> CONFUSION_POWDER = materialItem("confusing_powder");
    public static final RegiliteItem<MaterialItem> WITHERING_POWDER = materialItem("withering_powder");

    // endregion

    // skipped a few

    // region Gears

    public static final RegiliteItem<MaterialItem> GEAR_IRON = materialItem("iron_gear")
            .setTranslation("Infinity Bimetal Gear");

    public static final RegiliteItem<MaterialItem> GEAR_ENERGIZED = materialItem("energized_gear")
            .setTranslation("Energized Bimetal Gear");

    public static final RegiliteItem<MaterialItem> GEAR_VIBRANT = materialItem("vibrant_gear")
            .setTranslation("Vibrant Bimetal Gear");

    public static final RegiliteItem<MaterialItem> GEAR_DARK_STEEL = materialItem("dark_bimetal_gear")
            .setTranslation("Dark Bimetal Gear");

    // endregion

    // region Dyes

    public static final RegiliteItem<MaterialItem> DYE_GREEN = materialItem("organic_green_dye");

    public static final RegiliteItem<MaterialItem> DYE_BROWN = materialItem("organic_brown_dye");

    public static final RegiliteItem<MaterialItem> DYE_BLACK = materialItem("organic_black_dye");

    // endregion

    // region Misc Materials

    public static final RegiliteItem<MaterialItem> PHOTOVOLTAIC_PLATE = materialItem("photovoltaic_plate");

    public static final RegiliteItem<MaterialItem> NUTRITIOUS_STICK = materialItem("nutritious_stick");

    public static final RegiliteItem<MaterialItem> PLANT_MATTER_GREEN = materialItem("plant_matter_green")
            .setTranslation("Clippings and Trimmings");

    public static final RegiliteItem<MaterialItem> PLANT_MATTER_BROWN = materialItem("plant_matter_brown")
            .setTranslation("Twigs and Prunings");

    public static final RegiliteItem<MaterialItem> GLIDER_WING = materialItem("glider_wing");

    public static final RegiliteItem<MaterialItem> ANIMAL_TOKEN = materialItemGlinted("animal_token");
    public static final RegiliteItem<MaterialItem> MONSTER_TOKEN = materialItemGlinted("monster_token");
    public static final RegiliteItem<MaterialItem> PLAYER_TOKEN = materialItemGlinted("player_token");
    public static final RegiliteItem<MaterialItem> CAKE_BASE = materialItem("cake_base");
    public static final RegiliteItem<MaterialItem> BLACK_PAPER = materialItem("black_paper");
    public static final RegiliteItem<MaterialItem> CLAYED_GLOWSTONE = materialItem("clayed_glowstone");
    public static final RegiliteItem<MaterialItem> NETHERCOTTA = materialItem("nethercotta");
    public static final RegiliteItem<MaterialItem> REDSTONE_FILTER_BASE = materialItem("redstone_filter_base");

    public static final RegiliteItem<BrokenSpawnerItem> BROKEN_SPAWNER = ITEM_REGISTRY
            .registerItem("broken_spawner", BrokenSpawnerItem::new)
            .addCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SoulCapabilityProviders.COMPONENT_SOUL_BINDABLE_PROVIDER)
            .setModelProvider(ModelHelper::fakeBlockModel)
            .setTab(EIOCreativeTabs.MAIN)
            .setTab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(BrokenSpawnerItem.getPossibleStacks()));

    // endregion

    // region Grinding Balls

    // TODO: 20.6: Config for grinding balls?

    public static final RegiliteItem<MaterialItem> SOULARIUM_BALL = grindingBall("soularium_grinding_ball",
            new GrindingBallData(1.2F, 2.15F, 0.9F, 80000));

    public static final RegiliteItem<MaterialItem> CONDUCTIVE_ALLOY_BALL = grindingBall(
            "conductive_alloy_grinding_ball", new GrindingBallData(1.35F, 1.00F, 1.0F, 40000));

    public static final RegiliteItem<MaterialItem> PULSATING_ALLOY_BALL = grindingBall("pulsating_alloy_grinding_ball",
            new GrindingBallData(1.00F, 1.85F, 1.0F, 100000));

    public static final RegiliteItem<MaterialItem> REDSTONE_ALLOY_BALL = grindingBall("redstone_alloy_grinding_ball",
            new GrindingBallData(1.00F, 1.00F, 0.35F, 30000));

    public static final RegiliteItem<MaterialItem> ENERGETIC_ALLOY_BALL = grindingBall("energetic_alloy_grinding_ball",
            new GrindingBallData(1.6F, 1.1F, 1.1F, 80000));

    public static final RegiliteItem<MaterialItem> VIBRANT_ALLOY_BALL = grindingBall("vibrant_alloy_grinding_ball",
            new GrindingBallData(1.75F, 1.35F, 1.13F, 80000));

    public static final RegiliteItem<MaterialItem> COPPER_ALLOY_BALL = grindingBall("copper_alloy_grinding_ball",
            new GrindingBallData(1.2F, 1.65F, 0.8F, 40000));

    public static final RegiliteItem<MaterialItem> DARK_STEEL_BALL = grindingBall("dark_steel_grinding_ball",
            new GrindingBallData(1.35F, 2.00F, 0.7F, 125000));

    public static final RegiliteItem<MaterialItem> END_STEEL_BALL = grindingBall("end_steel_grinding_ball",
            new GrindingBallData(1.4F, 2.4F, 0.7F, 75000));

    // public static final Map<DyeColor, RegiliteItem<HangGliderItem>>
    // COLORED_HANG_GLIDERS = Util.make(() -> {
    // Map<DyeColor, RegiliteItem<HangGliderItem>> tempMap = new
    // EnumMap<>(DyeColor.class);
    // for (DyeColor color: DyeColor.values()) {
    // var entry = gliderItem(color.getName() + "_glider");
    // tempMap.put(color, entry);
    // }
    // return tempMap;
    // });

    public static final RegiliteItem<HangGliderItem> GLIDER = gliderItem("glider");

    private static RegiliteItem<MaterialItem> grindingBall(String name, GrindingBallData grindingBallData) {
        return ITEM_REGISTRY
                .registerItem(name,
                        props -> new MaterialItem(props.component(EnderIODataComponents.GRINDING_BALL, grindingBallData),
                                false))
                .setTab(EIOCreativeTabs.MAIN);
    }

    // endregion

    // region Builders

    private static RegiliteItem<HangGliderItem> gliderItem(String name) {
        return dumbItem(name, HangGliderItem::new).setTab(EIOCreativeTabs.GEAR)
        /* .setModelProvider((prov, ctx) -> GliderItemModel.create(ctx.get(), prov)) */;
    }

    private static RegiliteItem<MaterialItem> materialItem(String name) {
        return ITEM_REGISTRY.registerItem(name, props -> new MaterialItem(props, false)).setTab(EIOCreativeTabs.MAIN);
    }

    private static RegiliteItem<MaterialItem> materialItemGlinted(String name) {
        return ITEM_REGISTRY.registerItem(name, props -> new MaterialItem(props, true)).setTab(EIOCreativeTabs.MAIN);
    }

    // endregion

    // region Items

    // Soul vial uses a read-only ISoulBindable.
    // This is because the soul vial is a storage which can be used for binding, but is not directly bound to.
    public static final RegiliteItem<SoulVialItem> SOUL_VIAL = groupedItem("soul_vial", SoulVialItem::new,
            EIOCreativeTabs.SOULS).setTab(EIOCreativeTabs.GEAR)
                    .setTab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(SoulVialItem.getAllFilled()))
                    .setModelProvider((prov, ctx) -> prov.basicItem(ctx.get())
                            .override()
                            .predicate(SoulVialItem.FILLED_MODEL_PROPERTY, 1)
                            .model(prov.basicItem(EnderIO.rl("soul_vial_filled")))
                            .end())
                    .addCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SoulCapabilityProviders.READ_ONLY_COMPONENT_SOUL_BINDABLE_PROVIDER)
                    .addCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM, SoulCapabilityProviders.SINGLE_COMPONENT_SOUL_HANDLER_PROVIDER);

    public static final RegiliteItem<EnderiosItem> ENDERIOS = ITEM_REGISTRY
            .registerItem("enderios", EnderiosItem::new, new Item.Properties().stacksTo(1))
            .setTab(EIOCreativeTabs.MAIN)
            .setTranslation("\"Enderios\"");
    // endregion

    // region Tools
    public static final RegiliteItem<YetaWrenchItem> YETA_WRENCH = ITEM_REGISTRY
            .registerItem("yeta_wrench", YetaWrenchItem::new, new Item.Properties().stacksTo(1))
            .setTab(EIOCreativeTabs.GEAR);

    public static final RegiliteItem<LocationPrintoutItem> LOCATION_PRINTOUT = ITEM_REGISTRY
            .registerItem("location_printout", LocationPrintoutItem::new, new Item.Properties().stacksTo(1))
            .setTab(EIOCreativeTabs.GEAR);

    public static final RegiliteItem<CoordinateSelectorItem> COORDINATE_SELECTOR = ITEM_REGISTRY
            .registerItem("coordinate_selector", CoordinateSelectorItem::new, new Item.Properties().stacksTo(1))
            .setTab(EIOCreativeTabs.GEAR);

    public static final RegiliteItem<VoidVialItem> VOID_VIAL = ITEM_REGISTRY
            .registerItem("void_vial", VoidVialItem::new)
            .setTranslation("Vial of the Void")
            .setTab(EIOCreativeTabs.GEAR);

    public static final RegiliteItem<LevitationStaffItem> LEVITATION_STAFF = ITEM_REGISTRY
            .registerItem("staff_of_levity", LevitationStaffItem::new)
            .setTab(EIOCreativeTabs.GEAR, modifier -> EIOItems.LEVITATION_STAFF.get().addAllVariants(modifier))
            .addCapability(Capabilities.FluidHandler.ITEM, LevitationStaffItem.FLUID_HANDLER_PROVIDER)
            .apply(EIOItems::poweredToggledItemCapabilities);

    public static final RegiliteItem<TravelStaffItem> TRAVEL_STAFF = ITEM_REGISTRY
            .registerItem("staff_of_travelling", TravelStaffItem::new, new Item.Properties().stacksTo(1))
            .setTab(EIOCreativeTabs.GEAR, modifier -> EIOItems.TRAVEL_STAFF.get().addAllVariants(modifier))
            .addCapability(Capabilities.EnergyStorage.ITEM, TravelStaffItem.ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<ElectromagnetItem> ELECTROMAGNET = ITEM_REGISTRY
            .registerItem("electromagnet", ElectromagnetItem::new)
            .setTab(EIOCreativeTabs.GEAR, modifier -> EIOItems.ELECTROMAGNET.get().addAllVariants(modifier))
            .apply(EIOItems::poweredToggledItemCapabilities);

    public static final RegiliteItem<ColdFireIgniter> COLD_FIRE_IGNITER = ITEM_REGISTRY
            .registerItem("cold_fire_igniter", ColdFireIgniter::new)
            .setTab(EIOCreativeTabs.GEAR, modifier -> EIOItems.COLD_FIRE_IGNITER.get().addAllVariants(modifier)) // TODO:
                                                                                                                 // Might
                                                                                                                 // PR
                                                                                                                 // this
                                                                                                                 // to
                                                                                                                 // ITEM_REGISTRY
                                                                                                                 // so
                                                                                                                 // its
                                                                                                                 // nicer,
                                                                                                                 // but
                                                                                                                 // I
                                                                                                                 // like
                                                                                                                 // the
                                                                                                                 // footprint.
            .addCapability(Capabilities.FluidHandler.ITEM, ColdFireIgniter.FLUID_HANDLER_PROVIDER);

    // endregion

    // region filter

    public static final RegiliteItem<EnderItemFilterItem> BASIC_ITEM_FILTER = ITEM_REGISTRY
            .registerItem("basic_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BASIC))
            .setTab(EIOCreativeTabs.GEAR)
            .addCapability(EnderIOCapabilities.ITEM_FILTER, EnderItemFilterItem.ITEM_FILTER_PROVIDER)
            .addCapability(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER);

    public static final RegiliteItem<EnderItemFilterItem> ADVANCED_ITEM_FILTER = ITEM_REGISTRY
            .registerItem("advanced_item_filter",
                    props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.ADVANCED))
            .setTab(EIOCreativeTabs.GEAR)
            .addCapability(EnderIOCapabilities.ITEM_FILTER, EnderItemFilterItem.ITEM_FILTER_PROVIDER)
            .addCapability(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER);

    public static final RegiliteItem<EnderItemFilterItem> BIG_ITEM_FILTER = ITEM_REGISTRY
            .registerItem("big_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BIG))
            .setTab(EIOCreativeTabs.GEAR)
            .addCapability(EnderIOCapabilities.ITEM_FILTER, EnderItemFilterItem.ITEM_FILTER_PROVIDER)
            .addCapability(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER);

    public static final RegiliteItem<EnderItemFilterItem> BIG_ADVANCED_ITEM_FILTER = ITEM_REGISTRY
            .registerItem("big_advanced_item_filter",
                    props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BIG_ADVANCED))
            .setTab(EIOCreativeTabs.GEAR)
            .addCapability(EnderIOCapabilities.ITEM_FILTER, EnderItemFilterItem.ITEM_FILTER_PROVIDER)
            .addCapability(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER);

    public static final RegiliteItem<EnderFluidFilterItem> BASIC_FLUID_FILTER = ITEM_REGISTRY
            .registerItem("basic_fluid_filter",
                    props -> new EnderFluidFilterItem(props, EnderFluidFilterItem.Type.BASIC))
            .setTab(EIOCreativeTabs.GEAR)
            .addCapability(EnderIOCapabilities.FLUID_FILTER, EnderFluidFilterItem.FLUID_FILTER_PROVIDER)
            .addCapability(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER);

    public static final RegiliteItem<EnderSoulFilterItem> BASIC_SOUL_FILTER = ITEM_REGISTRY
            .registerItem("basic_soul_filter",
                    props -> new EnderSoulFilterItem(props, EnderSoulFilterItem.Type.BASIC))
            .setTab(EIOCreativeTabs.GEAR)
            .addCapability(EnderIOCapabilities.SOUL_FILTER, EnderSoulFilterItem.ENTITY_FILTER_PROVIDER)
            .addCapability(EnderIOCapabilities.FILTER_MENU_PROVIDER, AbstractFilterItem.FILTER_MENU_PROVIDER);

    // endregion

    // region description

    public static MutableComponent capacitorDescriptionBuilder(String type, String value, String description) {
        // TODO: Regilite general translation support.
        // return REGISTRATE.addLang("description", EnderIO.loc("capacitor." + type +
        // "." + value), description);
        return Component.empty();
    }

    // endregion

    // region Creative Tab Icons

    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_NONE = dumbItem("enderface_none",
            CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_ITEMS = dumbItem("enderface_items",
            CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_MATERIALS = dumbItem("enderface_materials",
            CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_MACHINES = dumbItem("enderface_machines",
            CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_CONDUITS = dumbItem("enderface_conduits",
            CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_MOBS = dumbItem("enderface_mobs",
            CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_INVPANEL = dumbItem("enderface_invpanel",
            CreativeTabIconItem::new);

    // endregion

    // region Helpers

    public static <T extends Item> RegiliteItem<T> dumbItem(String name, Function<Item.Properties, T> factory) {
        // TODO: remove from search.
        return ITEM_REGISTRY.registerItem(name, factory);// removeTab(CreativeModeTabs.SEARCH);
    }

    public static RegiliteItem<Item> dumbItem(String name) {
        return ITEM_REGISTRY.registerItem(name);
    }

    public static <T extends Item> RegiliteItem<T> groupedItem(String name, Function<Item.Properties, T> factory,
            ResourceKey<CreativeModeTab> tab) {
        return ITEM_REGISTRY.registerItem(name, factory).setTab(tab);
    }

    private static <T extends PoweredToggledItem> void poweredToggledItemCapabilities(RegiliteItem<T> item) {
        item.addCapability(Capabilities.EnergyStorage.ITEM, PoweredToggledItem.ENERGY_STORAGE_PROVIDER);
    }

    // endregion

    public static void register(IEventBus bus) {
        ITEMS.register(bus);

    	ENDERIOS.setModelProvider((prov, ctx) -> {});
    
        // XP rod rename
        ITEM_REGISTRY.addAlias(EnderIO.rl("experience_rod"), VOID_VIAL.getId());

        // Unified soul vials
        ITEM_REGISTRY.addAlias(EnderIO.rl("empty_soul_vial"), SOUL_VIAL.getId());
        ITEM_REGISTRY.addAlias(EnderIO.rl("filled_soul_vial"), SOUL_VIAL.getId());

		// Filter renames
        ITEM_REGISTRY.addAlias(EnderIO.rl("basic_filter"), BASIC_ITEM_FILTER.getId());
        ITEM_REGISTRY.addAlias(EnderIO.rl("advanced_filter"), ADVANCED_ITEM_FILTER.getId());
        ITEM_REGISTRY.addAlias(EnderIO.rl("fluid_filter"), BASIC_FLUID_FILTER.getId());
        ITEM_REGISTRY.addAlias(EnderIO.rl("entity_filter"), BASIC_SOUL_FILTER.getId());

        ITEM_REGISTRY.register(bus);
    }
}
