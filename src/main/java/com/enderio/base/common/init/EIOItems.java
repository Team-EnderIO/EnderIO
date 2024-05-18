package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorData;
import com.enderio.api.grindingball.GrindingBallData;
import com.enderio.base.common.item.capacitors.CapacitorItem;
import com.enderio.base.common.item.capacitors.LootCapacitorItem;
import com.enderio.base.common.item.filter.FluidFilter;
import com.enderio.base.common.item.filter.ItemFilter;
import com.enderio.base.common.item.misc.BrokenSpawnerItem;
import com.enderio.base.common.item.misc.CreativeTabIconItem;
import com.enderio.base.common.item.misc.EnderiosItem;
import com.enderio.base.common.item.misc.HangGliderItem;
import com.enderio.base.common.item.misc.LocationPrintoutItem;
import com.enderio.base.common.item.misc.MaterialItem;
import com.enderio.base.common.item.tool.ColdFireIgniter;
import com.enderio.base.common.item.tool.CoordinateSelectorItem;
import com.enderio.base.common.item.tool.ElectromagnetItem;
import com.enderio.base.common.item.tool.ExperienceRodItem;
import com.enderio.base.common.item.tool.LevitationStaffItem;
import com.enderio.base.common.item.tool.PoweredToggledItem;
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.base.common.item.tool.TravelStaffItem;
import com.enderio.base.common.item.tool.YetaWrenchItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.model.item.GliderItemModel;
import com.enderio.core.common.capability.FluidFilterCapability;
import com.enderio.core.common.capability.ItemFilterCapability;
import com.enderio.core.data.model.ModelHelper;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Function;

@SuppressWarnings("unused")
public class EIOItems {
    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();

    // region Alloys

    public static final RegiliteItem<MaterialItem> COPPER_ALLOY_INGOT = materialItem("copper_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_COPPER_ALLOY);
    public static final RegiliteItem<MaterialItem> ENERGETIC_ALLOY_INGOT = materialItem("energetic_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_ENERGETIC_ALLOY);
    public static final RegiliteItem<MaterialItem> VIBRANT_ALLOY_INGOT = materialItem("vibrant_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_VIBRANT_ALLOY);
    public static final RegiliteItem<MaterialItem> REDSTONE_ALLOY_INGOT = materialItem("redstone_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_REDSTONE_ALLOY);
    public static final RegiliteItem<MaterialItem> CONDUCTIVE_ALLOY_INGOT = materialItem("conductive_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY);
    public static final RegiliteItem<MaterialItem> PULSATING_ALLOY_INGOT = materialItem("pulsating_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_PULSATING_ALLOY);
    public static final RegiliteItem<MaterialItem> DARK_STEEL_INGOT = materialItem("dark_steel_ingot").addItemTags(EIOTags.Items.INGOTS_DARK_STEEL);
    public static final RegiliteItem<MaterialItem> SOULARIUM_INGOT = materialItem("soularium_ingot").addItemTags(EIOTags.Items.INGOTS_SOULARIUM);
    public static final RegiliteItem<MaterialItem> END_STEEL_INGOT = materialItem("end_steel_ingot").addItemTags(EIOTags.Items.INGOTS_END_STEEL);

    public static final RegiliteItem<MaterialItem> COPPER_ALLOY_NUGGET = materialItem("copper_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_COPPER_ALLOY);
    public static final RegiliteItem<MaterialItem> ENERGETIC_ALLOY_NUGGET = materialItem("energetic_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_ENERGETIC_ALLOY);
    public static final RegiliteItem<MaterialItem> VIBRANT_ALLOY_NUGGET = materialItem("vibrant_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_VIBRANT_ALLOY);
    public static final RegiliteItem<MaterialItem> REDSTONE_ALLOY_NUGGET = materialItem("redstone_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_REDSTONE_ALLOY);
    public static final RegiliteItem<MaterialItem> CONDUCTIVE_ALLOY_NUGGET = materialItem("conductive_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_CONDUCTIVE_ALLOY);
    public static final RegiliteItem<MaterialItem> PULSATING_ALLOY_NUGGET = materialItem("pulsating_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_PULSATING_ALLOY);
    public static final RegiliteItem<MaterialItem> DARK_STEEL_NUGGET = materialItem("dark_steel_nugget").addItemTags(EIOTags.Items.NUGGETS_DARK_STEEL);
    public static final RegiliteItem<MaterialItem> SOULARIUM_NUGGET = materialItem("soularium_nugget").addItemTags(EIOTags.Items.NUGGETS_SOULARIUM);
    public static final RegiliteItem<MaterialItem> END_STEEL_NUGGET = materialItem("end_steel_nugget").addItemTags(EIOTags.Items.NUGGETS_END_STEEL);

    // endregion

    // region Crafting Components

    public static final RegiliteItem<MaterialItem> SILICON = materialItem("silicon").addItemTags(EIOTags.Items.SILICON);

    public static final RegiliteItem<MaterialItem> GRAINS_OF_INFINITY = materialItem("grains_of_infinity")
        .addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
        .setTranslation("Grains of Infinity");

    public static final RegiliteItem<MaterialItem> INFINITY_ROD = materialItem("infinity_rod");

    public static final RegiliteItem<MaterialItem> CONDUIT_BINDER_COMPOSITE = materialItem("conduit_binder_composite");

    public static final RegiliteItem<MaterialItem> CONDUIT_BINDER = materialItem("conduit_binder");

    public static final RegiliteItem<MaterialItem> ZOMBIE_ELECTRODE = materialItem("zombie_electrode");

    public static final RegiliteItem<MaterialItem> Z_LOGIC_CONTROLLER = materialItem("z_logic_controller")
        .setTranslation("Z-Logic Controller");

    public static final RegiliteItem<MaterialItem> FRANK_N_ZOMBIE = materialItemGlinted("frank_n_zombie")
        .setTranslation("Frank'N'Zombie")
        .setModelProvider((prov, ctx) -> ModelHelper.mimicItem(prov, ctx, EIOItems.Z_LOGIC_CONTROLLER));

    public static final RegiliteItem<MaterialItem> ENDER_RESONATOR = materialItem("ender_resonator");

    public static final RegiliteItem<MaterialItem> SENTIENT_ENDER = materialItemGlinted("sentient_ender")
        .setModelProvider((prov, ctx) -> ModelHelper.mimicItem(prov, ctx, EIOItems.ENDER_RESONATOR));

    public static final RegiliteItem<MaterialItem> SKELETAL_CONTRACTOR = materialItem("skeletal_contractor");
    public static final RegiliteItem<MaterialItem> GUARDIAN_DIODE = materialItem("guardian_diode");

    // endregion

    // region Capacitors

    public static final RegiliteItem<CapacitorItem> BASIC_CAPACITOR = ITEM_REGISTRY
        .registerItem("basic_capacitor",
            props -> new CapacitorItem(
                props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(1))),
            new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.MAIN);

    public static final RegiliteItem<CapacitorItem> DOUBLE_LAYER_CAPACITOR = ITEM_REGISTRY
        .registerItem("double_layer_capacitor",
            props -> new CapacitorItem(
                props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(2))),
            new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.MAIN);

    public static final RegiliteItem<CapacitorItem> OCTADIC_CAPACITOR = ITEM_REGISTRY
        .registerItem("octadic_capacitor",
            props -> new CapacitorItem(
                props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(3))),
            new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.MAIN);

    public static final RegiliteItem<LootCapacitorItem> LOOT_CAPACITOR = ITEM_REGISTRY
        .registerItem("loot_capacitor", LootCapacitorItem::new, new Item.Properties()
            .stacksTo(1));

    // endregion

    // region Crystals

    public static final RegiliteItem<MaterialItem> PULSATING_CRYSTAL = materialItemGlinted("pulsating_crystal").addItemTags(EIOTags.Items.GEMS_PULSATING_CRYSTAL);
    public static final RegiliteItem<MaterialItem> VIBRANT_CRYSTAL = materialItemGlinted("vibrant_crystal").addItemTags(EIOTags.Items.GEMS_VIBRANT_CRYSTAL);
    public static final RegiliteItem<MaterialItem> ENDER_CRYSTAL = materialItemGlinted("ender_crystal").addItemTags(EIOTags.Items.GEMS_ENDER_CRYSTAL);
    public static final RegiliteItem<MaterialItem> ENTICING_CRYSTAL = materialItemGlinted("enticing_crystal").addItemTags(EIOTags.Items.GEMS_ENTICING_CRYSTAL);
    public static final RegiliteItem<MaterialItem> WEATHER_CRYSTAL = materialItemGlinted("weather_crystal").addItemTags(EIOTags.Items.GEMS_WEATHER_CRYSTAL);
    public static final RegiliteItem<MaterialItem> PRESCIENT_CRYSTAL = materialItemGlinted("prescient_crystal").addItemTags(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL);

    // endregion

    // region Powders and Fragments

    public static final RegiliteItem<MaterialItem> FLOUR = materialItem("flour");
    public static final RegiliteItem<MaterialItem> POWDERED_COAL = materialItem("powdered_coal")
        .addItemTags(EIOTags.Items.DUSTS_COAL);

    public static final RegiliteItem<MaterialItem> POWDERED_IRON = materialItem("powdered_iron")
        .addItemTags(EIOTags.Items.DUSTS_IRON);

    public static final RegiliteItem<MaterialItem> POWDERED_GOLD = materialItem("powdered_gold")
        .addItemTags(EIOTags.Items.DUSTS_GOLD);

    public static final RegiliteItem<MaterialItem> POWDERED_COPPER = materialItem("powdered_copper")
        .addItemTags(EIOTags.Items.DUSTS_COPPER);

    public static final RegiliteItem<MaterialItem> POWDERED_TIN = materialItem("powdered_tin")
        .addItemTags(EIOTags.Items.DUSTS_TIN); // TODO: hide if tin isn't present

    public static final RegiliteItem<MaterialItem> POWDERED_ENDER_PEARL = materialItem("powdered_ender_pearl")
        .addItemTags(EIOTags.Items.DUSTS_ENDER);

    public static final RegiliteItem<MaterialItem> POWDERED_OBSIDIAN = materialItem("powdered_obsidian")
        .addItemTags(EIOTags.Items.DUSTS_OBSIDIAN);

    public static final RegiliteItem<MaterialItem> POWDERED_COBALT = materialItem("powdered_cobalt")
        .addItemTags(EIOTags.Items.DUSTS_COBALT); // TODO: hide if cobalt isnt present

    public static final RegiliteItem<MaterialItem> POWDERED_LAPIS_LAZULI = materialItem("powdered_lapis_lazuli")
        .addItemTags(EIOTags.Items.DUSTS_LAPIS);

    public static final RegiliteItem<MaterialItem> POWDERED_QUARTZ = materialItem("powdered_quartz")
        .addItemTags(EIOTags.Items.DUSTS_QUARTZ);

    public static final RegiliteItem<MaterialItem> PRESCIENT_POWDER = materialItemGlinted("prescient_powder")
        .addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_PRESCIENCE)
        .setTranslation("Grains of Prescience");

    public static final RegiliteItem<MaterialItem> VIBRANT_POWDER = materialItemGlinted("vibrant_powder")
        .addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY)
        .setTranslation("Grains of Vibrancy");

    public static final RegiliteItem<MaterialItem> PULSATING_POWDER = materialItemGlinted("pulsating_powder")
        .addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY)
        .setTranslation("Grains of Piezallity");

    public static final RegiliteItem<MaterialItem> ENDER_CRYSTAL_POWDER = materialItemGlinted("ender_crystal_powder")
        .addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_THE_END)
        .setTranslation("Grains of the End");

    public static final RegiliteItem<MaterialItem> PHOTOVOLTAIC_COMPOSITE = materialItem("photovoltaic_composite");
    public static final RegiliteItem<MaterialItem> SOUL_POWDER = materialItem("soul_powder");
    public static final RegiliteItem<MaterialItem> CONFUSION_POWDER = materialItem("confusing_powder");
    public static final RegiliteItem<MaterialItem> WITHERING_POWDER = materialItem("withering_powder");

    // endregion

    // skipped a few

    // region Gears

    public static final RegiliteItem<MaterialItem> GEAR_WOOD = materialItem("wood_gear")
        .setTranslation("Wooden Gear")
        .addItemTags(EIOTags.Items.GEARS_WOOD);

    public static final RegiliteItem<MaterialItem> GEAR_STONE = materialItem("stone_gear")
        .setTranslation("Stone Compound Gear")
        .addItemTags(EIOTags.Items.GEARS_STONE);

    public static final RegiliteItem<MaterialItem> GEAR_IRON = materialItem("iron_gear")
        .setTranslation("Infinity Bimetal Gear")
        .addItemTags(EIOTags.Items.GEARS_IRON);

    public static final RegiliteItem<MaterialItem> GEAR_ENERGIZED = materialItem("energized_gear")
        .setTranslation("Energized Bimetal Gear")
        .addItemTags(EIOTags.Items.GEARS_ENERGIZED);

    public static final RegiliteItem<MaterialItem> GEAR_VIBRANT = materialItem("vibrant_gear")
        .setTranslation("Vibrant Bimetal Gear")
        .addItemTags(EIOTags.Items.GEARS_VIBRANT);

    public static final RegiliteItem<MaterialItem> GEAR_DARK_STEEL = materialItem("dark_bimetal_gear")
        .setTranslation("Dark Bimetal Gear")
        .addItemTags(EIOTags.Items.GEARS_DARK_STEEL);

    // endregion

    // region Dyes

    public static final RegiliteItem<MaterialItem> DYE_GREEN = materialItem("organic_green_dye")
        .addItemTags(Tags.Items.DYES_GREEN, Tags.Items.DYES);

    public static final RegiliteItem<MaterialItem> DYE_BROWN = materialItem("organic_brown_dye")
        .addItemTags(Tags.Items.DYES_BROWN, Tags.Items.DYES);

    public static final RegiliteItem<MaterialItem> DYE_BLACK = materialItem("organic_black_dye")
        .addItemTags(Tags.Items.DYES_BLACK, Tags.Items.DYES);

    // endregion

    // region Misc Materials

    public static final RegiliteItem<MaterialItem> PHOTOVOLTAIC_PLATE = materialItem("photovoltaic_plate")
        .setModelProvider((prov, ctx) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("block/pressure_plate_up")).texture("texture", prov.itemTexture(ctx.get())))
        ;

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
        .setModelProvider(ModelHelper::fakeBlockModel)
        .setTab(EIOCreativeTabs.MAIN)
        .setTab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(BrokenSpawnerItem.gePossibleStacks()));

    // endregion

    // region Grinding Balls

    // TODO: 20.6: Config for grinding balls?

    public static final RegiliteItem<MaterialItem> SOULARIUM_BALL = grindingBall("soularium_grinding_ball",
        new GrindingBallData(1.2F, 2.15F, 0.9F, 80000));

    public static final RegiliteItem<MaterialItem> CONDUCTIVE_ALLOY_BALL = grindingBall("conductive_alloy_grinding_ball",
        new GrindingBallData(1.35F, 1.00F, 1.0F, 40000));

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

    //    public static final Map<DyeColor, RegiliteItem<HangGliderItem>> COLORED_HANG_GLIDERS = Util.make(() -> {
    //       Map<DyeColor, RegiliteItem<HangGliderItem>> tempMap = new EnumMap<>(DyeColor.class);
    //       for (DyeColor color: DyeColor.values()) {
    //           var entry = gliderItem(color.getName() + "_glider");
    //           tempMap.put(color, entry);
    //       }
    //       return tempMap;
    //    });

    //    public static final RegiliteItem<HangGliderItem> GLIDER = gliderItem("glider");

    private static RegiliteItem<MaterialItem> grindingBall(String name, GrindingBallData grindingBallData) {
        return ITEM_REGISTRY
            .registerItem(name, props -> new MaterialItem(
                props
                    .component(EIODataComponents.GRINDING_BALL, grindingBallData),
                false))
            .setTab(EIOCreativeTabs.MAIN);
    }

    // endregion

    // region Builders

    private static RegiliteItem<HangGliderItem> gliderItem(String name) {
        return dumbItem(name, HangGliderItem::new)
            .addItemTags(EIOTags.Items.GLIDER)
            .setTab(EIOCreativeTabs.MAIN)
            .setModelProvider((prov, ctx) -> GliderItemModel.create(ctx.get(), prov));
    }

    private static RegiliteItem<MaterialItem> materialItem(String name) {
        return ITEM_REGISTRY
            .registerItem(name, props -> new MaterialItem(props, false))
            .setTab(EIOCreativeTabs.MAIN);
    }

    private static RegiliteItem<MaterialItem> materialItemGlinted(String name) {
        return ITEM_REGISTRY
            .registerItem(name, props -> new MaterialItem(props, true))
            .setTab(EIOCreativeTabs.MAIN);
    }

    // endregion

    // region Items

    public static final RegiliteItem<SoulVialItem> EMPTY_SOUL_VIAL = groupedItem("empty_soul_vial", SoulVialItem::new, EIOCreativeTabs.SOULS);

    public static final RegiliteItem<SoulVialItem> FILLED_SOUL_VIAL = ITEM_REGISTRY
        .registerItem("filled_soul_vial", SoulVialItem::new, new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(SoulVialItem.getAllFilled()))
        //TODO .removeTab(CreativeModeTabs.SEARCH)
        ;

    public static final RegiliteItem<EnderiosItem> ENDERIOS = ITEM_REGISTRY
        .registerItem("enderios", EnderiosItem::new, new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.MAIN)
        .setTranslation("\"Enderios\"");

    // endregion

    // region Tools
    public static final RegiliteItem<YetaWrenchItem> YETA_WRENCH = ITEM_REGISTRY
        .registerItem("yeta_wrench", YetaWrenchItem::new, new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.GEAR)
        .addItemTags(EIOTags.Items.WRENCH);

    public static final RegiliteItem<LocationPrintoutItem> LOCATION_PRINTOUT = ITEM_REGISTRY
        .registerItem("location_printout", LocationPrintoutItem::new, new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.GEAR);

    public static final RegiliteItem<CoordinateSelectorItem> COORDINATE_SELECTOR = ITEM_REGISTRY
        .registerItem("coordinate_selector", CoordinateSelectorItem::new, new Item.Properties().stacksTo(1))
        .setTab(EIOCreativeTabs.GEAR);

    public static final RegiliteItem<ExperienceRodItem> EXPERIENCE_ROD = ITEM_REGISTRY
        .registerItem("experience_rod", ExperienceRodItem::new)
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
        .setTab(EIOCreativeTabs.GEAR,
            modifier -> EIOItems.COLD_FIRE_IGNITER.get().addAllVariants(modifier)) // TODO: Might PR this to ITEM_REGISTRY so its nicer, but I like the footprint.
        .addCapability(Capabilities.FluidHandler.ITEM, ColdFireIgniter.FLUID_HANDLER_PROVIDER);

    // endregion

    // region filter

    public static final RegiliteItem<ItemFilter> BASIC_ITEM_FILTER = ITEM_REGISTRY
        .registerItem("basic_filter", properties -> new ItemFilter(properties.component(EIODataComponents.ITEM_FILTER, new ItemFilterCapability.Component(5))))
        .setTab(EIOCreativeTabs.GEAR)
        .addCapability(EIOCapabilities.Filter.ITEM, ItemFilter.FILTER_PROVIDER);

    public static final RegiliteItem<ItemFilter> ADVANCED_ITEM_FILTER = ITEM_REGISTRY
        .registerItem("advanced_filter", properties -> new ItemFilter(properties.component(EIODataComponents.ITEM_FILTER, new ItemFilterCapability.Component(10))))
        .setTab(EIOCreativeTabs.GEAR)
        .addCapability(EIOCapabilities.Filter.ITEM, ItemFilter.FILTER_PROVIDER);

    public static final RegiliteItem<FluidFilter> BASIC_FLUID_FILTER = ITEM_REGISTRY
        .registerItem("fluid_filter", properties -> new FluidFilter(properties.component(EIODataComponents.FLUID_FILTER, new FluidFilterCapability.Component(5))))
        .setTab(EIOCreativeTabs.GEAR)
        .addCapability(EIOCapabilities.Filter.ITEM, FluidFilter.FILTER_PROVIDER);

    // endregion

    // region description

    public static MutableComponent capacitorDescriptionBuilder(String type, String value, String description) {
        // TODO: Regilite general translation support.
        //return REGISTRATE.addLang("description", EnderIO.loc("capacitor." + type + "." + value), description);
        return Component.empty();
    }

    // endregion

    // region Creative Tab Icons

    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_NONE = dumbItem("enderface_none", CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_ITEMS = dumbItem("enderface_items", CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_MATERIALS = dumbItem("enderface_materials", CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_MACHINES = dumbItem("enderface_machines", CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_CONDUITS = dumbItem("enderface_conduits", CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_MOBS = dumbItem("enderface_mobs", CreativeTabIconItem::new);
    public static final RegiliteItem<CreativeTabIconItem> CREATIVE_ICON_INVPANEL = dumbItem("enderface_invpanel", CreativeTabIconItem::new);

    // endregion

    // region Helpers

    public static <T extends Item> RegiliteItem<T> dumbItem(String name, Function<Item.Properties, T> factory) {
        // TODO: remove from search.
        return ITEM_REGISTRY.registerItem(name, factory);//removeTab(CreativeModeTabs.SEARCH);
    }

    public static RegiliteItem<Item> dumbItem(String name) {
        return ITEM_REGISTRY.registerItem(name);
    }

    public static <T extends Item> RegiliteItem<T> groupedItem(String name, Function<Item.Properties, T> factory, ResourceKey<CreativeModeTab> tab) {
        return ITEM_REGISTRY.registerItem(name, factory).setTab(tab);
    }

    private static <T extends PoweredToggledItem> void poweredToggledItemCapabilities(RegiliteItem<T> item) {
        item.addCapability(Capabilities.EnergyStorage.ITEM, PoweredToggledItem.ENERGY_STORAGE_PROVIDER);
    }

    // endregion

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
