package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.conduits.facade.FacadeType;
import com.enderio.enderio.content.armory.DarkSteelSwordItem;
import com.enderio.enderio.content.broken_spawner.BrokenSpawnerItem;
import com.enderio.enderio.content.capacitors.CapacitorItem;
import com.enderio.enderio.content.capacitors.LootCapacitorItem;
import com.enderio.enderio.content.cold_fire.ColdFireIgniter;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.conduits.facades.ConduitFacadeItem;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilterItem;
import com.enderio.enderio.content.filters.item.general.EnderItemFilterItem;
import com.enderio.enderio.content.filters.item.limited.LimitedItemFilterItem;
import com.enderio.enderio.content.filters.redstone.RedstoneFilterItem;
import com.enderio.enderio.content.filters.soul.EnderSoulFilterItem;
import com.enderio.enderio.content.fun.EnderiosItem;
import com.enderio.enderio.content.tools.ElectromagnetItem;
import com.enderio.enderio.content.tools.LevitationStaffItem;
import com.enderio.enderio.content.tools.YetaWrenchItem;
import com.enderio.enderio.content.tools.coordinate_selector.CoordinateSelectorItem;
import com.enderio.enderio.content.tools.coordinate_selector.LocationPrintoutItem;
import com.enderio.enderio.content.tools.hang_glider.HangGliderItem;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.content.tools.vials.VoidVialItem;
import com.enderio.enderio.content.travel.TravelStaffItem;
import com.enderio.enderio.foundation.item.CreativeTabIconItem;
import com.enderio.enderio.foundation.item.MaterialItem;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EIOItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnderIO.MOD_ID);

    // region Alloys

    public static final RegistryObject<Item> CONDUCTIVE_ALLOY_INGOT = basic("conductive_alloy_ingot");
    public static final RegistryObject<Item> ENERGETIC_ALLOY_INGOT = basic("energetic_alloy_ingot");
    public static final RegistryObject<Item> VIBRANT_ALLOY_INGOT = basic("vibrant_alloy_ingot");
    public static final RegistryObject<Item> REDSTONE_ALLOY_INGOT = basic("redstone_alloy_ingot");
    public static final RegistryObject<Item> PULSATING_ALLOY_INGOT = basic("pulsating_alloy_ingot");
    public static final RegistryObject<Item> DARK_STEEL_INGOT = basic("dark_steel_ingot");
    public static final RegistryObject<Item> SOULARIUM_INGOT = basic("soularium_ingot");
    public static final RegistryObject<Item> END_STEEL_INGOT = basic("end_steel_ingot");

    public static final RegistryObject<Item> CONDUCTIVE_ALLOY_NUGGET = basic("conductive_alloy_nugget");
    public static final RegistryObject<Item> ENERGETIC_ALLOY_NUGGET = basic("energetic_alloy_nugget");
    public static final RegistryObject<Item> VIBRANT_ALLOY_NUGGET = basic("vibrant_alloy_nugget");
    public static final RegistryObject<Item> REDSTONE_ALLOY_NUGGET = basic("redstone_alloy_nugget");
    public static final RegistryObject<Item> PULSATING_ALLOY_NUGGET = basic("pulsating_alloy_nugget");
    public static final RegistryObject<Item> DARK_STEEL_NUGGET = basic("dark_steel_nugget");
    public static final RegistryObject<Item> SOULARIUM_NUGGET = basic("soularium_nugget");
    public static final RegistryObject<Item> END_STEEL_NUGGET = basic("end_steel_nugget");

    // endregion

    // region Grinding Balls

    public static final RegistryObject<Item> ENERGETIC_ALLOY_BALL = grindingBall("energetic_alloy_grinding_ball");

    public static final RegistryObject<Item> VIBRANT_ALLOY_BALL = grindingBall("vibrant_alloy_grinding_ball");

    public static final RegistryObject<Item> REDSTONE_ALLOY_BALL = grindingBall("redstone_alloy_grinding_ball");

    public static final RegistryObject<Item> CONDUCTIVE_ALLOY_BALL = grindingBall("conductive_alloy_grinding_ball");

    public static final RegistryObject<Item> PULSATING_ALLOY_BALL = grindingBall("pulsating_alloy_grinding_ball");

    public static final RegistryObject<Item> DARK_STEEL_BALL = grindingBall("dark_steel_grinding_ball");

    public static final RegistryObject<Item> SOULARIUM_BALL = grindingBall("soularium_grinding_ball");

    public static final RegistryObject<Item> END_STEEL_BALL = grindingBall("end_steel_grinding_ball");

    private static RegistryObject<Item> grindingBall(String name) {
        return registerItem(name, Item::new, new Item.Properties());
    }

    // endregion

    // region Crafting Components

    public static final RegistryObject<Item> SILICON = basic("silicon");
    public static final RegistryObject<Item> GRAINS_OF_INFINITY = basic("grains_of_infinity");
    public static final RegistryObject<Item> INFINITY_ROD = basic("infinity_rod");
    public static final RegistryObject<Item> CONDUIT_BINDER_COMPOSITE = basic("conduit_binder_composite");
    public static final RegistryObject<Item> CONDUIT_BINDER = basic("conduit_binder");

    public static final RegistryObject<Item> GEAR_IRON = basic("iron_gear");
    public static final RegistryObject<Item> GEAR_ENERGIZED = basic("energized_gear");
    public static final RegistryObject<Item> GEAR_VIBRANT = basic("vibrant_gear");
    public static final RegistryObject<Item> GEAR_DARK_STEEL = basic("dark_bimetal_gear");

    public static final RegistryObject<Item> ZOMBIE_ELECTRODE = basic("zombie_electrode");
    public static final RegistryObject<Item> Z_LOGIC_CONTROLLER = basic("z_logic_controller");
    public static final RegistryObject<Item> FRANK_N_ZOMBIE = basicGlinted("frank_n_zombie");
    public static final RegistryObject<Item> ENDER_RESONATOR = basic("ender_resonator");
    public static final RegistryObject<Item> SENTIENT_ENDER = basicGlinted("sentient_ender");
    public static final RegistryObject<Item> SKELETAL_CONTRACTOR = basic("skeletal_contractor");
    public static final RegistryObject<Item> GUARDIAN_DIODE = basic("guardian_diode");
    public static final RegistryObject<Item> SUSPICIOUS_SEED = lore("suspicious_seed", EIOCommonLang.SUSPICIOUS_SEED_LORE);

    // endregion

    // region Capacitors

    public static final RegistryObject<CapacitorItem> BASIC_CAPACITOR = registerItem("basic_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(1)));

    public static final RegistryObject<CapacitorItem> DOUBLE_LAYER_CAPACITOR = registerItem("double_layer_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(2)));

    public static final RegistryObject<CapacitorItem> OCTADIC_CAPACITOR = registerItem("octadic_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(3)));

    public static final RegistryObject<LootCapacitorItem> LOOT_CAPACITOR = registerItem("loot_capacitor",
        LootCapacitorItem::new, new Item.Properties().stacksTo(1));

    // endregion

    // region Crystals

    public static final RegistryObject<Item> PULSATING_CRYSTAL = basicGlinted("pulsating_crystal");
    public static final RegistryObject<Item> VIBRANT_CRYSTAL = basicGlinted("vibrant_crystal");
    public static final RegistryObject<Item> ENDER_CRYSTAL = basicGlinted("ender_crystal");
    public static final RegistryObject<Item> ENTICING_CRYSTAL = basicGlinted("enticing_crystal");
    public static final RegistryObject<Item> WEATHER_CRYSTAL = basicGlinted("weather_crystal");
    public static final RegistryObject<Item> PRESCIENT_CRYSTAL = basicGlinted("prescient_crystal");

    // endregion

    // region Powders and Fragments

    public static final RegistryObject<Item> POWDERED_COAL = basic("powdered_coal");
    public static final RegistryObject<Item> POWDERED_IRON = basic("powdered_iron");
    public static final RegistryObject<Item> POWDERED_GOLD = basic("powdered_gold");
    public static final RegistryObject<Item> POWDERED_COPPER = basic("powdered_copper");
    public static final RegistryObject<Item> POWDERED_TIN = basic("powdered_tin");
    public static final RegistryObject<Item> POWDERED_ENDER_PEARL = basic("powdered_ender_pearl");
    public static final RegistryObject<Item> POWDERED_OBSIDIAN = basic("powdered_obsidian");
    public static final RegistryObject<Item> POWDERED_LAPIS_LAZULI = basic("powdered_lapis_lazuli");
    public static final RegistryObject<Item> POWDERED_QUARTZ = basic("powdered_quartz");
    public static final RegistryObject<Item> PRESCIENT_POWDER = basic("prescient_powder");
    public static final RegistryObject<Item> VIBRANT_POWDER = basic("vibrant_powder");
    public static final RegistryObject<Item> PULSATING_POWDER = basic("pulsating_powder");
    public static final RegistryObject<Item> ENDER_CRYSTAL_POWDER = basic("ender_crystal_powder");
    public static final RegistryObject<Item> PHOTOVOLTAIC_COMPOSITE = basic("photovoltaic_composite");
    public static final RegistryObject<Item> SOUL_POWDER = basic("soul_powder");
    public static final RegistryObject<Item> CONFUSION_POWDER = basic("confusing_powder");
    public static final RegistryObject<Item> WITHERING_POWDER = basic("withering_powder");

    // endregion

    // region Misc Materials

    public static final RegistryObject<Item> PHOTOVOLTAIC_PLATE = basic("photovoltaic_plate");
    public static final RegistryObject<Item> NUTRITIOUS_STICK = basic("nutritious_stick");
    public static final RegistryObject<Item> PLANT_MATTER_GREEN = basic("plant_matter_green");
    public static final RegistryObject<Item> PLANT_MATTER_BROWN = basic("plant_matter_brown");
    public static final RegistryObject<Item> GLIDER_WING = basic("glider_wing");
    public static final RegistryObject<Item> ANIMAL_TOKEN = basicGlinted("animal_token");
    public static final RegistryObject<Item> MONSTER_TOKEN = basicGlinted("monster_token");
    public static final RegistryObject<Item> PLAYER_TOKEN = basicGlinted("player_token");

    public static final RegistryObject<BrokenSpawnerItem> BROKEN_SPAWNER = registerItem("broken_spawner", BrokenSpawnerItem::new);

    // endregion

    // region Gliders

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

    public static final RegistryObject<HangGliderItem> GLIDER = gliderItem("glider");

    private static RegistryObject<HangGliderItem> gliderItem(String name) {
        return registerItem(name, HangGliderItem::new);
        /* .setModelProvider((prov, ctx) -> GliderItemModel.create(ctx.get(), prov)) */
    }

    // endregion

    // region Fun

    public static final RegistryObject<EnderiosItem> ENDERIOS = registerItem("enderios", EnderiosItem::new,
        new Item.Properties().stacksTo(1));

    // endregion

    // region Tools

    public static final RegistryObject<SoulVialItem> SOUL_VIAL = registerItem("soul_vial", SoulVialItem::new);
    public static final RegistryObject<VoidVialItem> VOID_VIAL = registerItem("void_vial", VoidVialItem::new);
    public static final RegistryObject<YetaWrenchItem> YETA_WRENCH = registerItem("yeta_wrench", YetaWrenchItem::new);
    public static final RegistryObject<CoordinateSelectorItem> COORDINATE_SELECTOR = registerItem("coordinate_selector", CoordinateSelectorItem::new);
    public static final RegistryObject<LocationPrintoutItem> LOCATION_PRINTOUT = registerItem("location_printout", LocationPrintoutItem::new);
    public static final RegistryObject<LevitationStaffItem> LEVITATION_STAFF = registerItem("staff_of_levity", LevitationStaffItem::new);
    public static final RegistryObject<TravelStaffItem> TRAVEL_STAFF = registerItem("staff_of_travelling", TravelStaffItem::new);
    public static final RegistryObject<ElectromagnetItem> ELECTROMAGNET = registerItem("electromagnet", ElectromagnetItem::new);
    public static final RegistryObject<ColdFireIgniter> COLD_FIRE_IGNITER = registerItem("cold_fire_igniter", ColdFireIgniter::new);
    public static final RegistryObject<ConduitProbeItem> CONDUIT_PROBE = registerItem("conduit_probe", ConduitProbeItem::new);

    public static final RegistryObject<DarkSteelSwordItem> DARK_STEEL_SWORD = registerItem("dark_steel_sword", DarkSteelSwordItem::new,
        new Item.Properties().durability(2000));

    // endregion

    // region Conduits

    // While these are block items, I think they're weird enough to be considered separate items.

    public static final RegistryObject<ConduitBlockItem> CONDUIT = registerItem("conduit", ConduitBlockItem::new);

    // TODO: Why is facade type being stored as a component... it could just be part of the block item
    public static final RegistryObject<ConduitFacadeItem> CONDUIT_FACADE = registerItem("conduit_facade", ConduitFacadeItem::new,
        new Item.Properties().component(EIODataComponents.FACADE_TYPE, FacadeType.BASIC));

    public static final RegistryObject<ConduitFacadeItem> TRANSPARENT_CONDUIT_FACADE = registerItem("transparent_conduit_facade", ConduitFacadeItem::new,
        new Item.Properties().component(EIODataComponents.FACADE_TYPE, FacadeType.TRANSPARENT));

    public static final RegistryObject<ConduitFacadeItem> HARDENED_CONDUIT_FACADE = registerItem("hardened_conduit_facade", ConduitFacadeItem::new,
        new Item.Properties().component(EIODataComponents.FACADE_TYPE, FacadeType.HARDENED));

    public static final RegistryObject<ConduitFacadeItem> TRANSPARENT_HARDENED_CONDUIT_FACADE = registerItem("transparent_hardened_conduit_facade",
        ConduitFacadeItem::new, new Item.Properties().component(EIODataComponents.FACADE_TYPE, FacadeType.TRANSPARENT_HARDENED));

    // endregion

    // region Filters

    public static final RegistryObject<EnderItemFilterItem> BASIC_ITEM_FILTER = registerItem("basic_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BASIC));
    public static final RegistryObject<EnderItemFilterItem> BIG_ITEM_FILTER = registerItem("big_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BIG));
    public static final RegistryObject<EnderItemFilterItem> ADVANCED_ITEM_FILTER = registerItem("advanced_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.ADVANCED));

    public static final RegistryObject<EnderItemFilterItem> BIG_ADVANCED_ITEM_FILTER = registerItem("big_advanced_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BIG_ADVANCED));

    public static final RegistryObject<LimitedItemFilterItem> LIMITED_ITEM_FILTER = registerItem("limited_item_filter", LimitedItemFilterItem::new);

    public static final RegistryObject<EnderFluidFilterItem> BASIC_FLUID_FILTER = registerItem("basic_fluid_filter", props -> new EnderFluidFilterItem(props, EnderFluidFilterItem.Type.BASIC));

    public static final RegistryObject<EnderSoulFilterItem> BASIC_SOUL_FILTER = registerItem("basic_soul_filter", props -> new EnderSoulFilterItem(props, EnderSoulFilterItem.Type.BASIC));

    public static final RegistryObject<Item> REDSTONE_FILTER_BASE = basic("redstone_filter_base");

    public static final RegistryObject<RedstoneFilterItem> NOT_FILTER = registerItem("redstone_not_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.NOT));
    public static final RegistryObject<RedstoneFilterItem> OR_FILTER = registerItem("redstone_or_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.OR));
    public static final RegistryObject<RedstoneFilterItem> AND_FILTER = registerItem("redstone_and_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.AND));
    public static final RegistryObject<RedstoneFilterItem> NOR_FILTER = registerItem("redstone_nor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.NOR));
    public static final RegistryObject<RedstoneFilterItem> NAND_FILTER = registerItem("redstone_nand_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.NAND));
    public static final RegistryObject<RedstoneFilterItem> XOR_FILTER = registerItem("redstone_xor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.XOR));
    public static final RegistryObject<RedstoneFilterItem> XNOR_FILTER = registerItem("redstone_xnor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.XNOR));
    public static final RegistryObject<RedstoneFilterItem> TLATCH_FILTER = registerItem("redstone_toggle_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.TLATCH));
    public static final RegistryObject<RedstoneFilterItem> COUNT_FILTER = registerItem("redstone_counting_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.COUNT));
    public static final RegistryObject<RedstoneFilterItem> SENSOR_FILTER = registerItem("redstone_sensor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.SENSOR));
    public static final RegistryObject<RedstoneFilterItem> TIMER_FILTER = registerItem("redstone_timer_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.TIMER));

    // endregion

    // region Creative Tab Icon

    public static final RegistryObject<CreativeTabIconItem> CREATIVE_ICON = registerItem("creative_tab_icon", CreativeTabIconItem::new);

    // endregion

    private static RegistryObject<Item> basic(String name) {
        return registerItem(name, Item::new);
    }

    private static RegistryObject<Item> basicGlinted(String name) {
        return registerItem(name, p -> new MaterialItem(p, true));
    }
    
    private static <T extends Item> RegistryObject<T> registerItem(String name, Function<Item.Properties, T> factory) {
        return registerItem(name, factory, new Item.Properties());
    }
    
    private static <T extends Item> RegistryObject<T> registerItem(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
        return ITEMS.register(name, () -> factory.apply(properties));
    }

    private static RegistryObject<Item> lore(String name, Component lore) {
        return registerItem(name, p -> new MaterialItem(p, true), new Item.Properties()
            .rarity(Rarity.RARE)
            .component(DataComponents.LORE, new ItemLore(List.of(lore))));
    }

    public static void register(IEventBus bus) {
        // TODO: 1.20.1 - aliases
//        // XP rod rename
//        ITEMS.addAlias(EnderIO.rl("experience_rod"), VOID_VIAL.getId());
//
//        // Unified soul vials
//        ITEMS.addAlias(EnderIO.rl("empty_soul_vial"), SOUL_VIAL.getId());
//        ITEMS.addAlias(EnderIO.rl("filled_soul_vial"), SOUL_VIAL.getId());
//
//		// Filter renames
//        ITEMS.addAlias(EnderIO.rl("basic_filter"), BASIC_ITEM_FILTER.getId());
//        ITEMS.addAlias(EnderIO.rl("advanced_filter"), ADVANCED_ITEM_FILTER.getId());
//        ITEMS.addAlias(EnderIO.rl("fluid_filter"), BASIC_FLUID_FILTER.getId());
//        ITEMS.addAlias(EnderIO.rl("entity_filter"), BASIC_SOUL_FILTER.getId());
//
//        // Copper Alloy
//        ITEMS.addAlias(EnderIO.rl("copper_alloy_ingot"), CONDUCTIVE_ALLOY_INGOT.getId());
//        ITEMS.addAlias(EnderIO.rl("copper_alloy_nugget"), CONDUCTIVE_ALLOY_NUGGET.getId());
//        ITEMS.addAlias(EnderIO.rl("copper_alloy_grinding_ball"), CONDUCTIVE_ALLOY_BALL.getId());

        ITEMS.register(bus);
    }
}
