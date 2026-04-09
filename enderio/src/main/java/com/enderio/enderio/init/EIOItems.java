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
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@SuppressWarnings("unused")
public class EIOItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnderIO.MOD_ID);

    // region Alloys

    public static final DeferredItem<Item> CONDUCTIVE_ALLOY_INGOT = basic("conductive_alloy_ingot");
    public static final DeferredItem<Item> ENERGETIC_ALLOY_INGOT = basic("energetic_alloy_ingot");
    public static final DeferredItem<Item> VIBRANT_ALLOY_INGOT = basic("vibrant_alloy_ingot");
    public static final DeferredItem<Item> REDSTONE_ALLOY_INGOT = basic("redstone_alloy_ingot");
    public static final DeferredItem<Item> PULSATING_ALLOY_INGOT = basic("pulsating_alloy_ingot");
    public static final DeferredItem<Item> DARK_STEEL_INGOT = basic("dark_steel_ingot");
    public static final DeferredItem<Item> SOULARIUM_INGOT = basic("soularium_ingot");
    public static final DeferredItem<Item> END_STEEL_INGOT = basic("end_steel_ingot");

    public static final DeferredItem<Item> CONDUCTIVE_ALLOY_NUGGET = basic("conductive_alloy_nugget");
    public static final DeferredItem<Item> ENERGETIC_ALLOY_NUGGET = basic("energetic_alloy_nugget");
    public static final DeferredItem<Item> VIBRANT_ALLOY_NUGGET = basic("vibrant_alloy_nugget");
    public static final DeferredItem<Item> REDSTONE_ALLOY_NUGGET = basic("redstone_alloy_nugget");
    public static final DeferredItem<Item> PULSATING_ALLOY_NUGGET = basic("pulsating_alloy_nugget");
    public static final DeferredItem<Item> DARK_STEEL_NUGGET = basic("dark_steel_nugget");
    public static final DeferredItem<Item> SOULARIUM_NUGGET = basic("soularium_nugget");
    public static final DeferredItem<Item> END_STEEL_NUGGET = basic("end_steel_nugget");

    // endregion

    // region Grinding Balls

    public static final DeferredItem<Item> ENERGETIC_ALLOY_BALL = grindingBall("energetic_alloy_grinding_ball");

    public static final DeferredItem<Item> VIBRANT_ALLOY_BALL = grindingBall("vibrant_alloy_grinding_ball");

    public static final DeferredItem<Item> REDSTONE_ALLOY_BALL = grindingBall("redstone_alloy_grinding_ball");

    public static final DeferredItem<Item> CONDUCTIVE_ALLOY_BALL = grindingBall("conductive_alloy_grinding_ball");

    public static final DeferredItem<Item> PULSATING_ALLOY_BALL = grindingBall("pulsating_alloy_grinding_ball");

    public static final DeferredItem<Item> DARK_STEEL_BALL = grindingBall("dark_steel_grinding_ball");

    public static final DeferredItem<Item> SOULARIUM_BALL = grindingBall("soularium_grinding_ball");

    public static final DeferredItem<Item> END_STEEL_BALL = grindingBall("end_steel_grinding_ball");

    private static DeferredItem<Item> grindingBall(String name) {
        return ITEMS.registerItem(name, Item::new, Item.Properties::new);
    }

    // endregion

    // region Crafting Components

    public static final DeferredItem<Item> SILICON = basic("silicon");
    public static final DeferredItem<Item> GRAINS_OF_INFINITY = basic("grains_of_infinity");
    public static final DeferredItem<Item> INFINITY_ROD = basic("infinity_rod");
    public static final DeferredItem<Item> CONDUIT_BINDER_COMPOSITE = basic("conduit_binder_composite");
    public static final DeferredItem<Item> CONDUIT_BINDER = basic("conduit_binder");

    public static final DeferredItem<Item> GEAR_IRON = basic("iron_gear");
    public static final DeferredItem<Item> GEAR_ENERGIZED = basic("energized_gear");
    public static final DeferredItem<Item> GEAR_VIBRANT = basic("vibrant_gear");
    public static final DeferredItem<Item> GEAR_DARK_STEEL = basic("dark_bimetal_gear");

    public static final DeferredItem<Item> ZOMBIE_ELECTRODE = basic("zombie_electrode");
    public static final DeferredItem<Item> Z_LOGIC_CONTROLLER = basic("z_logic_controller");
    public static final DeferredItem<Item> FRANK_N_ZOMBIE = basicGlinted("frank_n_zombie");
    public static final DeferredItem<Item> ENDER_RESONATOR = basic("ender_resonator");
    public static final DeferredItem<Item> SENTIENT_ENDER = basicGlinted("sentient_ender");
    public static final DeferredItem<Item> SKELETAL_CONTRACTOR = basic("skeletal_contractor");
    public static final DeferredItem<Item> GUARDIAN_DIODE = basic("guardian_diode");
    public static final DeferredItem<Item> SUSPICIOUS_SEED = lore("suspicious_seed", EIOCommonLang.SUSPICIOUS_SEED_LORE);

    // endregion

    // region Capacitors

    public static final DeferredItem<CapacitorItem> BASIC_CAPACITOR = ITEMS.registerItem("basic_capacitor",
        CapacitorItem::new, p -> p.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(1)));

    public static final DeferredItem<CapacitorItem> DOUBLE_LAYER_CAPACITOR = ITEMS.registerItem("double_layer_capacitor",
        CapacitorItem::new, p -> p.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(2)));

    public static final DeferredItem<CapacitorItem> OCTADIC_CAPACITOR = ITEMS.registerItem("octadic_capacitor",
        CapacitorItem::new, p -> p.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(3)));

    public static final DeferredItem<LootCapacitorItem> LOOT_CAPACITOR = ITEMS.registerItem("loot_capacitor",
        LootCapacitorItem::new, p -> p.stacksTo(1));

    // endregion

    // region Crystals

    public static final DeferredItem<Item> PULSATING_CRYSTAL = basicGlinted("pulsating_crystal");
    public static final DeferredItem<Item> VIBRANT_CRYSTAL = basicGlinted("vibrant_crystal");
    public static final DeferredItem<Item> ENDER_CRYSTAL = basicGlinted("ender_crystal");
    public static final DeferredItem<Item> ENTICING_CRYSTAL = basicGlinted("enticing_crystal");
    public static final DeferredItem<Item> WEATHER_CRYSTAL = basicGlinted("weather_crystal");
    public static final DeferredItem<Item> PRESCIENT_CRYSTAL = basicGlinted("prescient_crystal");

    // endregion

    // region Powders and Fragments

    public static final DeferredItem<Item> POWDERED_COAL = basic("powdered_coal");
    public static final DeferredItem<Item> POWDERED_IRON = basic("powdered_iron");
    public static final DeferredItem<Item> POWDERED_GOLD = basic("powdered_gold");
    public static final DeferredItem<Item> POWDERED_COPPER = basic("powdered_copper");
    public static final DeferredItem<Item> POWDERED_TIN = basic("powdered_tin");
    public static final DeferredItem<Item> POWDERED_ENDER_PEARL = basic("powdered_ender_pearl");
    public static final DeferredItem<Item> POWDERED_OBSIDIAN = basic("powdered_obsidian");
    public static final DeferredItem<Item> POWDERED_LAPIS_LAZULI = basic("powdered_lapis_lazuli");
    public static final DeferredItem<Item> POWDERED_QUARTZ = basic("powdered_quartz");
    public static final DeferredItem<Item> PRESCIENT_POWDER = basic("prescient_powder");
    public static final DeferredItem<Item> VIBRANT_POWDER = basic("vibrant_powder");
    public static final DeferredItem<Item> PULSATING_POWDER = basic("pulsating_powder");
    public static final DeferredItem<Item> ENDER_CRYSTAL_POWDER = basic("ender_crystal_powder");
    public static final DeferredItem<Item> PHOTOVOLTAIC_COMPOSITE = basic("photovoltaic_composite");
    public static final DeferredItem<Item> SOUL_POWDER = basic("soul_powder");
    public static final DeferredItem<Item> CONFUSION_POWDER = basic("confusing_powder");
    public static final DeferredItem<Item> WITHERING_POWDER = basic("withering_powder");

    // endregion

    // region Misc Materials

    public static final DeferredItem<Item> PHOTOVOLTAIC_PLATE = basic("photovoltaic_plate");
    public static final DeferredItem<Item> NUTRITIOUS_STICK = basic("nutritious_stick");
    public static final DeferredItem<Item> PLANT_MATTER_GREEN = basic("plant_matter_green");
    public static final DeferredItem<Item> PLANT_MATTER_BROWN = basic("plant_matter_brown");
    public static final DeferredItem<Item> GLIDER_WING = basic("glider_wing");
    public static final DeferredItem<Item> ANIMAL_TOKEN = basicGlinted("animal_token");
    public static final DeferredItem<Item> MONSTER_TOKEN = basicGlinted("monster_token");
    public static final DeferredItem<Item> PLAYER_TOKEN = basicGlinted("player_token");

    public static final DeferredItem<BrokenSpawnerItem> BROKEN_SPAWNER = ITEMS.registerItem("broken_spawner", BrokenSpawnerItem::new);

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

    public static final DeferredItem<HangGliderItem> GLIDER = gliderItem("glider");

    private static DeferredItem<HangGliderItem> gliderItem(String name) {
        return ITEMS.registerItem(name, HangGliderItem::new);
        /* .setModelProvider((prov, ctx) -> GliderItemModel.create(ctx.get(), prov)) */
    }

    // endregion

    // region Fun

    public static final DeferredItem<EnderiosItem> ENDERIOS = ITEMS.registerItem("enderios", EnderiosItem::new,
        p -> p.stacksTo(1));

    // endregion

    // region Tools

    public static final DeferredItem<SoulVialItem> SOUL_VIAL = ITEMS.registerItem("soul_vial", SoulVialItem::new);
    public static final DeferredItem<VoidVialItem> VOID_VIAL = ITEMS.registerItem("void_vial", VoidVialItem::new);
    public static final DeferredItem<YetaWrenchItem> YETA_WRENCH = ITEMS.registerItem("yeta_wrench", YetaWrenchItem::new);
    public static final DeferredItem<CoordinateSelectorItem> COORDINATE_SELECTOR = ITEMS.registerItem("coordinate_selector", CoordinateSelectorItem::new);
    public static final DeferredItem<LocationPrintoutItem> LOCATION_PRINTOUT = ITEMS.registerItem("location_printout", LocationPrintoutItem::new);
    public static final DeferredItem<LevitationStaffItem> LEVITATION_STAFF = ITEMS.registerItem("staff_of_levity", LevitationStaffItem::new);
    public static final DeferredItem<TravelStaffItem> TRAVEL_STAFF = ITEMS.registerItem("staff_of_travelling", TravelStaffItem::new);
    public static final DeferredItem<ElectromagnetItem> ELECTROMAGNET = ITEMS.registerItem("electromagnet", ElectromagnetItem::new);
    public static final DeferredItem<ColdFireIgniter> COLD_FIRE_IGNITER = ITEMS.registerItem("cold_fire_igniter", ColdFireIgniter::new);
    public static final DeferredItem<ConduitProbeItem> CONDUIT_PROBE = ITEMS.registerItem("conduit_probe", ConduitProbeItem::new);

    public static final DeferredItem<DarkSteelSwordItem> DARK_STEEL_SWORD = ITEMS.registerItem("dark_steel_sword", DarkSteelSwordItem::new,
        p -> p.durability(2000));

    // endregion

    // region Conduits

    // While these are block items, I think they're weird enough to be considered separate items.

    public static final DeferredItem<ConduitBlockItem> CONDUIT = ITEMS.registerItem("conduit", ConduitBlockItem::new);

    // TODO: Why is facade type being stored as a component... it could just be part of the block item
    public static final DeferredItem<ConduitFacadeItem> CONDUIT_FACADE = ITEMS.registerItem("conduit_facade", ConduitFacadeItem::new,
        p -> p.component(EIODataComponents.FACADE_TYPE, FacadeType.BASIC));

    public static final DeferredItem<ConduitFacadeItem> TRANSPARENT_CONDUIT_FACADE = ITEMS.registerItem("transparent_conduit_facade", ConduitFacadeItem::new,
        p -> p.component(EIODataComponents.FACADE_TYPE, FacadeType.TRANSPARENT));

    public static final DeferredItem<ConduitFacadeItem> HARDENED_CONDUIT_FACADE = ITEMS.registerItem("hardened_conduit_facade", ConduitFacadeItem::new,
        p -> p.component(EIODataComponents.FACADE_TYPE, FacadeType.HARDENED));

    public static final DeferredItem<ConduitFacadeItem> TRANSPARENT_HARDENED_CONDUIT_FACADE = ITEMS.registerItem("transparent_hardened_conduit_facade",
        ConduitFacadeItem::new, p -> p.component(EIODataComponents.FACADE_TYPE, FacadeType.TRANSPARENT_HARDENED));

    // endregion

    // region Filters

    public static final DeferredItem<EnderItemFilterItem> BASIC_ITEM_FILTER = ITEMS.registerItem("basic_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BASIC));
    public static final DeferredItem<EnderItemFilterItem> BIG_ITEM_FILTER = ITEMS.registerItem("big_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BIG));
    public static final DeferredItem<EnderItemFilterItem> ADVANCED_ITEM_FILTER = ITEMS.registerItem("advanced_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.ADVANCED));

    public static final DeferredItem<EnderItemFilterItem> BIG_ADVANCED_ITEM_FILTER = ITEMS.registerItem("big_advanced_item_filter", props -> new EnderItemFilterItem(props, EnderItemFilterItem.Type.BIG_ADVANCED));

    public static final DeferredItem<LimitedItemFilterItem> LIMITED_ITEM_FILTER = ITEMS.registerItem("limited_item_filter", LimitedItemFilterItem::new);

    public static final DeferredItem<EnderFluidFilterItem> BASIC_FLUID_FILTER = ITEMS.registerItem("basic_fluid_filter", props -> new EnderFluidFilterItem(props, EnderFluidFilterItem.Type.BASIC));

    public static final DeferredItem<EnderSoulFilterItem> BASIC_SOUL_FILTER = ITEMS.registerItem("basic_soul_filter", props -> new EnderSoulFilterItem(props, EnderSoulFilterItem.Type.BASIC));

    public static final DeferredItem<Item> REDSTONE_FILTER_BASE = basic("redstone_filter_base");

    public static final DeferredItem<RedstoneFilterItem> NOT_FILTER = ITEMS.registerItem("redstone_not_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.NOT));
    public static final DeferredItem<RedstoneFilterItem> OR_FILTER = ITEMS.registerItem("redstone_or_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.OR));
    public static final DeferredItem<RedstoneFilterItem> AND_FILTER = ITEMS.registerItem("redstone_and_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.AND));
    public static final DeferredItem<RedstoneFilterItem> NOR_FILTER = ITEMS.registerItem("redstone_nor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.NOR));
    public static final DeferredItem<RedstoneFilterItem> NAND_FILTER = ITEMS.registerItem("redstone_nand_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.NAND));
    public static final DeferredItem<RedstoneFilterItem> XOR_FILTER = ITEMS.registerItem("redstone_xor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.XOR));
    public static final DeferredItem<RedstoneFilterItem> XNOR_FILTER = ITEMS.registerItem("redstone_xnor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.XNOR));
    public static final DeferredItem<RedstoneFilterItem> TLATCH_FILTER = ITEMS.registerItem("redstone_toggle_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.TLATCH));
    public static final DeferredItem<RedstoneFilterItem> COUNT_FILTER = ITEMS.registerItem("redstone_counting_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.COUNT));
    public static final DeferredItem<RedstoneFilterItem> SENSOR_FILTER = ITEMS.registerItem("redstone_sensor_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.SENSOR));
    public static final DeferredItem<RedstoneFilterItem> TIMER_FILTER = ITEMS.registerItem("redstone_timer_filter", p -> new RedstoneFilterItem(p, RedstoneFilterItem.Type.TIMER));

    // endregion

    // region Creative Tab Icon

    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON = ITEMS.registerItem("creative_tab_icon",
        CreativeTabIconItem::new, p -> p.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));

    // endregion

    private static DeferredItem<Item> basic(String name) {
        return ITEMS.registerItem(name, Item::new);
    }

    private static DeferredItem<Item> basicGlinted(String name) {
        return ITEMS.registerItem(name, Item::new, p -> p.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    }

    private static DeferredItem<Item> lore(String name, Component lore) {
        return ITEMS.registerItem(name, Item::new, p -> p
            .rarity(Rarity.RARE)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            .component(DataComponents.LORE, new ItemLore(List.of(lore))));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
