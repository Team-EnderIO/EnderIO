package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.capacitor.DefaultCapacitorData;
import com.enderio.base.common.item.capacitors.FixedCapacitorItem;
import com.enderio.base.common.item.capacitors.LootCapacitorItem;
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
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.base.common.item.tool.TravelStaffItem;
import com.enderio.base.common.item.tool.YetaWrenchItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.model.item.GliderItemModel;
import com.enderio.core.common.registry.EnderDeferredItem;
import com.enderio.core.common.registry.EnderItemRegistry;
import com.enderio.core.data.model.EIOModel;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Function;

@SuppressWarnings("unused")
public class EIOItems {
    private static final EnderItemRegistry ITEMS = EnderItemRegistry.createRegistry(EnderIO.MODID);

    // region Alloys

    public static final EnderDeferredItem<MaterialItem> COPPER_ALLOY_INGOT = materialItem("copper_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_COPPER_ALLOY);
    public static final EnderDeferredItem<MaterialItem> ENERGETIC_ALLOY_INGOT = materialItem("energetic_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_ENERGETIC_ALLOY);
    public static final EnderDeferredItem<MaterialItem> VIBRANT_ALLOY_INGOT = materialItem("vibrant_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_VIBRANT_ALLOY);
    public static final EnderDeferredItem<MaterialItem> REDSTONE_ALLOY_INGOT = materialItem("redstone_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_REDSTONE_ALLOY);
    public static final EnderDeferredItem<MaterialItem> CONDUCTIVE_ALLOY_INGOT = materialItem("conductive_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY);
    public static final EnderDeferredItem<MaterialItem> PULSATING_ALLOY_INGOT = materialItem("pulsating_alloy_ingot").addItemTags(EIOTags.Items.INGOTS_PULSATING_ALLOY);
    public static final EnderDeferredItem<MaterialItem> DARK_STEEL_INGOT = materialItem("dark_steel_ingot").addItemTags(EIOTags.Items.INGOTS_DARK_STEEL);
    public static final EnderDeferredItem<MaterialItem> SOULARIUM_INGOT = materialItem("soularium_ingot").addItemTags(EIOTags.Items.INGOTS_SOULARIUM);
    public static final EnderDeferredItem<MaterialItem> END_STEEL_INGOT = materialItem("end_steel_ingot").addItemTags(EIOTags.Items.INGOTS_END_STEEL);

    public static final EnderDeferredItem<MaterialItem> COPPER_ALLOY_NUGGET = materialItem("copper_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_COPPER_ALLOY);
    public static final EnderDeferredItem<MaterialItem> ENERGETIC_ALLOY_NUGGET = materialItem("energetic_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_ENERGETIC_ALLOY);
    public static final EnderDeferredItem<MaterialItem> VIBRANT_ALLOY_NUGGET = materialItem("vibrant_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_VIBRANT_ALLOY);
    public static final EnderDeferredItem<MaterialItem> REDSTONE_ALLOY_NUGGET = materialItem("redstone_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_REDSTONE_ALLOY);
    public static final EnderDeferredItem<MaterialItem> CONDUCTIVE_ALLOY_NUGGET = materialItem("conductive_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_CONDUCTIVE_ALLOY);
    public static final EnderDeferredItem<MaterialItem> PULSATING_ALLOY_NUGGET = materialItem("pulsating_alloy_nugget").addItemTags(EIOTags.Items.NUGGETS_PULSATING_ALLOY);
    public static final EnderDeferredItem<MaterialItem> DARK_STEEL_NUGGET = materialItem("dark_steel_nugget").addItemTags(EIOTags.Items.NUGGETS_DARK_STEEL);
    public static final EnderDeferredItem<MaterialItem> SOULARIUM_NUGGET = materialItem("soularium_nugget").addItemTags(EIOTags.Items.NUGGETS_SOULARIUM);
    public static final EnderDeferredItem<MaterialItem> END_STEEL_NUGGET = materialItem("end_steel_nugget").addItemTags(EIOTags.Items.NUGGETS_END_STEEL);

    // endregion

    // region Crafting Components

    public static final EnderDeferredItem<MaterialItem> SILICON = materialItem("silicon").addItemTags(EIOTags.Items.SILICON);

    public static final EnderDeferredItem<MaterialItem> GRAINS_OF_INFINITY = materialItem("grains_of_infinity").addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY).setTranslation("Grains of Infinity");

    public static final EnderDeferredItem<MaterialItem> INFINITY_ROD = materialItem("infinity_rod");

    public static final EnderDeferredItem<MaterialItem> CONDUIT_BINDER_COMPOSITE = materialItem("conduit_binder_composite");

    public static final EnderDeferredItem<MaterialItem> CONDUIT_BINDER = materialItem("conduit_binder");

    public static final EnderDeferredItem<MaterialItem> ZOMBIE_ELECTRODE = materialItem("zombie_electrode");

    public static final EnderDeferredItem<MaterialItem> Z_LOGIC_CONTROLLER = materialItem("z_logic_controller").setTranslation("Z-Logic Controller");

    public static final EnderDeferredItem<MaterialItem> FRANK_N_ZOMBIE = materialItemGlinted("frank_n_zombie")
        .setTranslation("Frank'N'Zombie")
        .setModelProvider((prov, item) -> EIOModel.mimicItem(item, EIOItems.Z_LOGIC_CONTROLLER, prov));

    public static final EnderDeferredItem<MaterialItem> ENDER_RESONATOR = materialItem("ender_resonator");

    public static final EnderDeferredItem<MaterialItem> SENTIENT_ENDER = materialItemGlinted("sentient_ender")
        .setModelProvider((prov, item) -> EIOModel.mimicItem(item, EIOItems.ENDER_RESONATOR, prov));

    public static final EnderDeferredItem<MaterialItem> SKELETAL_CONTRACTOR = materialItem("skeletal_contractor");
    public static final EnderDeferredItem<MaterialItem> GUARDIAN_DIODE = materialItem("guardian_diode");

    // endregion

    // region Capacitors

    public static final EnderDeferredItem<FixedCapacitorItem> BASIC_CAPACITOR = ITEMS
        .registerItem("basic_capacitor", props -> new FixedCapacitorItem(DefaultCapacitorData.BASIC, props))
        .setTab(EIOCreativeTabs.MAIN);

    public static final EnderDeferredItem<FixedCapacitorItem> DOUBLE_LAYER_CAPACITOR = ITEMS
        .registerItem("double_layer_capacitor", props -> new FixedCapacitorItem(DefaultCapacitorData.DOUBLE_LAYER, props))
        .setTab(EIOCreativeTabs.MAIN);

    public static final EnderDeferredItem<FixedCapacitorItem> OCTADIC_CAPACITOR = ITEMS
        .registerItem("octadic_capacitor", props -> new FixedCapacitorItem(DefaultCapacitorData.OCTADIC, props))
        .setTab(EIOCreativeTabs.MAIN);

    public static final EnderDeferredItem<LootCapacitorItem> LOOT_CAPACITOR = ITEMS
        .registerItem("loot_capacitor", properties -> new LootCapacitorItem(new Item.Properties().stacksTo(1)));

    // endregion

    // region Crystals

    public static final EnderDeferredItem<MaterialItem> PULSATING_CRYSTAL = materialItemGlinted("pulsating_crystal").addItemTags(EIOTags.Items.GEMS_PULSATING_CRYSTAL);
    public static final EnderDeferredItem<MaterialItem> VIBRANT_CRYSTAL = materialItemGlinted("vibrant_crystal").addItemTags(EIOTags.Items.GEMS_VIBRANT_CRYSTAL);
    public static final EnderDeferredItem<MaterialItem> ENDER_CRYSTAL = materialItemGlinted("ender_crystal").addItemTags(EIOTags.Items.GEMS_ENDER_CRYSTAL);
    public static final EnderDeferredItem<MaterialItem> ENTICING_CRYSTAL = materialItemGlinted("enticing_crystal").addItemTags(EIOTags.Items.GEMS_ENTICING_CRYSTAL);
    public static final EnderDeferredItem<MaterialItem> WEATHER_CRYSTAL = materialItemGlinted("weather_crystal").addItemTags(EIOTags.Items.GEMS_WEATHER_CRYSTAL);
    public static final EnderDeferredItem<MaterialItem> PRESCIENT_CRYSTAL = materialItemGlinted("prescient_crystal").addItemTags(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL);

    // endregion

    // region Powders and Fragments

    public static final EnderDeferredItem<MaterialItem> FLOUR = materialItem("flour");
    public static final EnderDeferredItem<MaterialItem> POWDERED_COAL = materialItem("powdered_coal").addItemTags(EIOTags.Items.DUSTS_COAL);
    public static final EnderDeferredItem<MaterialItem> POWDERED_IRON = materialItem("powdered_iron").addItemTags(EIOTags.Items.DUSTS_IRON);
    public static final EnderDeferredItem<MaterialItem> POWDERED_GOLD = materialItem("powdered_gold").addItemTags(EIOTags.Items.DUSTS_GOLD);
    public static final EnderDeferredItem<MaterialItem> POWDERED_COPPER = materialItem("powdered_copper").addItemTags(EIOTags.Items.DUSTS_COPPER);
    public static final EnderDeferredItem<MaterialItem> POWDERED_TIN = materialItem("powdered_tin")
        .addItemTags(EIOTags.Items.DUSTS_TIN); // TODO: hide if tin isn't present
    public static final EnderDeferredItem<MaterialItem> POWDERED_ENDER_PEARL = materialItem("powdered_ender_pearl").addItemTags(EIOTags.Items.DUSTS_ENDER);
    public static final EnderDeferredItem<MaterialItem> POWDERED_OBSIDIAN = materialItem("powdered_obsidian").addItemTags(EIOTags.Items.DUSTS_OBSIDIAN);
    public static final EnderDeferredItem<MaterialItem> POWDERED_COBALT = materialItem("powdered_cobalt")
        .addItemTags(EIOTags.Items.DUSTS_COBALT); // TODO: hide if cobalt isnt present
    public static final EnderDeferredItem<MaterialItem> POWDERED_LAPIS_LAZULI = materialItem("powdered_lapis_lazuli").addItemTags(EIOTags.Items.DUSTS_LAPIS);
    public static final EnderDeferredItem<MaterialItem> POWDERED_QUARTZ = materialItem("powdered_quartz").addItemTags(EIOTags.Items.DUSTS_QUARTZ);

    public static final EnderDeferredItem<MaterialItem> PRESCIENT_POWDER = materialItemGlinted("prescient_powder").addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_PRESCIENCE).setTranslation("Grains of Prescience");

    public static final EnderDeferredItem<MaterialItem> VIBRANT_POWDER = materialItemGlinted("vibrant_powder").addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY).setTranslation("Grains of Vibrancy");

    public static final EnderDeferredItem<MaterialItem> PULSATING_POWDER = materialItemGlinted("pulsating_powder").addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY).setTranslation("Grains of Piezallity");

    public static final EnderDeferredItem<MaterialItem> ENDER_CRYSTAL_POWDER = materialItemGlinted("ender_crystal_powder").addItemTags(EIOTags.Items.DUSTS_GRAINS_OF_THE_END).setTranslation("Grains of the End");

    public static final EnderDeferredItem<MaterialItem> PHOTOVOLTAIC_COMPOSITE = materialItem("photovoltaic_composite");
    public static final EnderDeferredItem<MaterialItem> SOUL_POWDER = materialItem("soul_powder");
    public static final EnderDeferredItem<MaterialItem> CONFUSION_POWDER = materialItem("confusing_powder");
    public static final EnderDeferredItem<MaterialItem> WITHERING_POWDER = materialItem("withering_powder");

    // endregion

    // skipped a few

    // region Gears

    public static final EnderDeferredItem<MaterialItem> GEAR_WOOD = materialItem("wood_gear").setTranslation("Wooden Gear").addItemTags(EIOTags.Items.GEARS_WOOD);

    public static final EnderDeferredItem<MaterialItem> GEAR_STONE = materialItem("stone_gear").setTranslation("Stone Compound Gear").addItemTags(EIOTags.Items.GEARS_STONE);

    public static final EnderDeferredItem<MaterialItem> GEAR_IRON = materialItem("iron_gear").setTranslation("Infinity Bimetal Gear").addItemTags(EIOTags.Items.GEARS_IRON);

    public static final EnderDeferredItem<MaterialItem> GEAR_ENERGIZED = materialItem("energized_gear").setTranslation("Energized Bimetal Gear").addItemTags(EIOTags.Items.GEARS_ENERGIZED);

    public static final EnderDeferredItem<MaterialItem> GEAR_VIBRANT = materialItem("vibrant_gear").setTranslation("Vibrant Bimetal Gear").addItemTags(EIOTags.Items.GEARS_VIBRANT);

    public static final EnderDeferredItem<MaterialItem> GEAR_DARK_STEEL = materialItem("dark_bimetal_gear").setTranslation("Dark Bimetal Gear").addItemTags(EIOTags.Items.GEARS_DARK_STEEL);

    // endregion

    // region Dyes

    public static final EnderDeferredItem<MaterialItem> DYE_GREEN = materialItem("organic_green_dye").addItemTags(Tags.Items.DYES_GREEN, Tags.Items.DYES);

    public static final EnderDeferredItem<MaterialItem> DYE_BROWN = materialItem("organic_brown_dye").addItemTags(Tags.Items.DYES_BROWN, Tags.Items.DYES);

    public static final EnderDeferredItem<MaterialItem> DYE_BLACK = materialItem("organic_black_dye").addItemTags(Tags.Items.DYES_BLACK, Tags.Items.DYES);

    // endregion

    // region Misc Materials

    public static final EnderDeferredItem<MaterialItem> PHOTOVOLTAIC_PLATE = materialItem("photovoltaic_plate")
        .setModelProvider((prov, item) -> prov.withExistingParent("photovoltaic_plate", prov.mcLoc("block/pressure_plate_up")).texture("texture", prov.itemTexture(item)));

    public static final EnderDeferredItem<MaterialItem> NUTRITIOUS_STICK = materialItem("nutritious_stick");

    public static final EnderDeferredItem<MaterialItem> PLANT_MATTER_GREEN = materialItem("plant_matter_green").setTranslation("Clippings and Trimmings");

    public static final EnderDeferredItem<MaterialItem> PLANT_MATTER_BROWN = materialItem("plant_matter_brown").setTranslation("Twigs and Prunings");

    public static final EnderDeferredItem<MaterialItem> GLIDER_WING = materialItem("glider_wing");

    public static final EnderDeferredItem<MaterialItem> ANIMAL_TOKEN = materialItemGlinted("animal_token");
    public static final EnderDeferredItem<MaterialItem> MONSTER_TOKEN = materialItemGlinted("monster_token");
    public static final EnderDeferredItem<MaterialItem> PLAYER_TOKEN = materialItemGlinted("player_token");
    public static final EnderDeferredItem<MaterialItem> CAKE_BASE = materialItem("cake_base");
    public static final EnderDeferredItem<MaterialItem> BLACK_PAPER = materialItem("black_paper");
    public static final EnderDeferredItem<MaterialItem> CLAYED_GLOWSTONE = materialItem("clayed_glowstone");
    public static final EnderDeferredItem<MaterialItem> NETHERCOTTA = materialItem("nethercotta");
    public static final EnderDeferredItem<MaterialItem> REDSTONE_FILTER_BASE = materialItem("redstone_filter_base");

    public static final EnderDeferredItem<BrokenSpawnerItem> BROKEN_SPAWNER = ITEMS
        .registerItem("broken_spawner", BrokenSpawnerItem::new)
        .setModelProvider(EIOModel::fakeBlockModel)
        .setTab(EIOCreativeTabs.MAIN);
        //.setTab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(BrokenSpawnerItem.gePossibleStacks())); TODO, Multiple tabs + filter

    // endregion

    // region GrindingBalls

    public static final EnderDeferredItem<MaterialItem> SOULARIUM_BALL = materialItem("soularium_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> CONDUCTIVE_ALLOY_BALL = materialItem("conductive_alloy_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> PULSATING_ALLOY_BALL = materialItem("pulsating_alloy_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> REDSTONE_ALLOY_BALL = materialItem("redstone_alloy_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> ENERGETIC_ALLOY_BALL = materialItem("energetic_alloy_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> VIBRANT_ALLOY_BALL = materialItem("vibrant_alloy_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> COPPER_ALLOY_BALL = materialItem("copper_alloy_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> DARK_STEEL_BALL = materialItem("dark_steel_grinding_ball");
    public static final EnderDeferredItem<MaterialItem> END_STEEL_BALL = materialItem("end_steel_grinding_ball");
    //    public static final Map<DyeColor, ItemEntry<HangGliderItem>> COLORED_HANG_GLIDERS = Util.make(() -> {
    //       Map<DyeColor, ItemEntry<HangGliderItem>> tempMap = new EnumMap<>(DyeColor.class);
    //       for (DyeColor color: DyeColor.values()) {
    //           var entry = gliderItem(color.getName() + "_glider");
    //           tempMap.put(color, entry.register());
    //       }
    //       return tempMap;
    //    });

    //    public static final ItemEntry<HangGliderItem> GLIDER = gliderItem("glider").register();

    // endregion

    // region Builders

    private static EnderDeferredItem<HangGliderItem> gliderItem(String name) {
        return dumbItem(name, HangGliderItem::new)
            .addItemTags(EIOTags.Items.GLIDER)
            .setTab(EIOCreativeTabs.MAIN)
            .setModelProvider(GliderItemModel::create);
    }

    private static EnderDeferredItem<MaterialItem> materialItem(String name) {
        return ITEMS.registerItem(name, props -> new MaterialItem(props, false)).setTab(EIOCreativeTabs.MAIN);
    }

    //  private static ItemBuilder<MaterialItem, Registrate> dependMaterialItem(String name, Tag<Item> dependency) {
    //    return REGISTRATE.item(name, props -> new MaterialItem(props, false, dependency))
    //        .group(() -> EnderIO.TAB_MAIN));
    //  }

    private static EnderDeferredItem<MaterialItem> materialItemGlinted(String name) {
        return ITEMS.registerItem(name, props -> new MaterialItem(props, true)).setTab(EIOCreativeTabs.MAIN);
    }

    // endregion

    // region Items

    // TODO: Will need sorted once we have added more.

    public static final EnderDeferredItem<SoulVialItem> EMPTY_SOUL_VIAL = groupedItem("empty_soul_vial", SoulVialItem::new, EIOCreativeTabs.SOULS);

    public static final EnderDeferredItem<SoulVialItem> FILLED_SOUL_VIAL = ITEMS
        .registerItem("filled_soul_vial", properties -> new SoulVialItem(new Item.Properties().stacksTo(1)))
        //.tab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(SoulVialItem.getAllFilled())) TODO
        .setTab(EIOCreativeTabs.SOULS);
        //.removeTab(CreativeModeTabs.SEARCH)

    public static final EnderDeferredItem<EnderiosItem> ENDERIOS = ITEMS
        .registerItem("enderios", properties -> new EnderiosItem(new Item.Properties().stacksTo(1)))
        .setTab(EIOCreativeTabs.MAIN)
        .setTranslation("\"Enderios\"");

    // endregion

    // region Tools
    public static final EnderDeferredItem<YetaWrenchItem> YETA_WRENCH = ITEMS
        .registerItem("yeta_wrench", properties -> new YetaWrenchItem(new Item.Properties().stacksTo(1)))
        .setTab(EIOCreativeTabs.GEAR)
        .addItemTags(EIOTags.Items.WRENCH);
    public static final EnderDeferredItem<LocationPrintoutItem> LOCATION_PRINTOUT = ITEMS
        .registerItem("location_printout", properties -> new LocationPrintoutItem(new Item.Properties().stacksTo(1)))
        .setTab(EIOCreativeTabs.GEAR);

    public static final EnderDeferredItem<CoordinateSelectorItem> COORDINATE_SELECTOR = ITEMS
        .registerItem("coordinate_selector", properties -> new CoordinateSelectorItem(new Item.Properties().stacksTo(1)))
        .setTab(EIOCreativeTabs.GEAR);

    public static final EnderDeferredItem<ExperienceRodItem> EXPERIENCE_ROD = ITEMS
        .registerItem("experience_rod", ExperienceRodItem::new)
        .setTab(EIOCreativeTabs.GEAR);

    public static final EnderDeferredItem<LevitationStaffItem> LEVITATION_STAFF = ITEMS
        .registerItem("staff_of_levity", properties -> new LevitationStaffItem(new Item.Properties().stacksTo(1)))
        //.tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.LEVITATION_STAFF.get().addAllVariants(modifier))
        .setTab(EIOCreativeTabs.GEAR);

    public static final EnderDeferredItem<TravelStaffItem> TRAVEL_STAFF = ITEMS
        .registerItem("staff_of_travelling", properties -> new TravelStaffItem(new Item.Properties().stacksTo(1)))
        //.tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.TRAVEL_STAFF.get().addAllVariants(modifier))
        .setTab(EIOCreativeTabs.GEAR);

    public static final EnderDeferredItem<ElectromagnetItem> ELECTROMAGNET = ITEMS
        .registerItem("electromagnet", properties -> new ElectromagnetItem(new Item.Properties().stacksTo(1)))
        //.tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.ELECTROMAGNET.get().addAllVariants(modifier))
        .setTab(EIOCreativeTabs.GEAR);

    public static final EnderDeferredItem<ColdFireIgniter> COLD_FIRE_IGNITER = ITEMS
        .registerItem("cold_fire_igniter", ColdFireIgniter::new)
        //.tab(EIOCreativeTabs.GEAR,
        //    modifier -> EIOItems.COLD_FIRE_IGNITER.get().addAllVariants(modifier)) // TODO: Might PR this to Registrate so its nicer, but I like the footprint.
        .setTab(EIOCreativeTabs.GEAR);


    // endregion

    // region Creative Tab Icons

    public static final EnderDeferredItem<CreativeTabIconItem> CREATIVE_ICON_NONE = dumbItem("enderface_none", CreativeTabIconItem::new);
    public static final EnderDeferredItem<CreativeTabIconItem> CREATIVE_ICON_ITEMS = dumbItem("enderface_items", CreativeTabIconItem::new);
    public static final EnderDeferredItem<CreativeTabIconItem> CREATIVE_ICON_MATERIALS = dumbItem("enderface_materials", CreativeTabIconItem::new);
    public static final EnderDeferredItem<CreativeTabIconItem> CREATIVE_ICON_MACHINES = dumbItem("enderface_machines", CreativeTabIconItem::new);
    public static final EnderDeferredItem<CreativeTabIconItem> CREATIVE_ICON_CONDUITS = dumbItem("enderface_conduits", CreativeTabIconItem::new);
    public static final EnderDeferredItem<CreativeTabIconItem> CREATIVE_ICON_MOBS = dumbItem("enderface_mobs", CreativeTabIconItem::new);
    public static final EnderDeferredItem<CreativeTabIconItem> CREATIVE_ICON_INVPANEL = dumbItem("enderface_invpanel", CreativeTabIconItem::new);

    // endregion

    // region Helpers

    public static <T extends Item> EnderDeferredItem<T> dumbItem(String name, Function<Item.Properties, T> factory) {
        return ITEMS.registerItem(name, factory); //.removeTab(CreativeModeTabs.SEARCH); TODO remove tab
    }

    public static <T extends Item> EnderDeferredItem<T> groupedItem(String name, Function<Item.Properties, T> factory, ResourceKey<CreativeModeTab> tab) {
        return ITEMS.registerItem(name, factory).setTab(tab);
    }

    // endregion

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
