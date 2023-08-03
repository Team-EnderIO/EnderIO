package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.capacitor.DefaultCapacitorData;
import com.enderio.base.common.item.capacitors.FixedCapacitorItem;
import com.enderio.base.common.item.capacitors.LootCapacitorItem;
import com.enderio.base.common.item.darksteel.DarkSteelSwordItem;
import com.enderio.base.common.item.misc.*;
import com.enderio.base.common.item.tool.*;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.model.item.GliderItemModel;
import com.enderio.base.data.model.item.RotatingItemModel;
import com.enderio.core.data.model.EIOModel;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

@SuppressWarnings("unused")
public class EIOItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region Alloys

    public static final ItemEntry<MaterialItem> COPPER_ALLOY_INGOT = materialItem("copper_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> ENERGETIC_ALLOY_INGOT = materialItem("energetic_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> VIBRANT_ALLOY_INGOT = materialItem("vibrant_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> REDSTONE_ALLOY_INGOT = materialItem("redstone_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> CONDUCTIVE_ALLOY_INGOT = materialItem("conductive_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> PULSATING_ALLOY_INGOT = materialItem("pulsating_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> DARK_STEEL_INGOT = materialItem("dark_steel_ingot").register();
    public static final ItemEntry<MaterialItem> SOULARIUM_INGOT = materialItem("soularium_ingot").register();
    public static final ItemEntry<MaterialItem> END_STEEL_INGOT = materialItem("end_steel_ingot").register();

    public static final ItemEntry<MaterialItem> COPPER_ALLOY_NUGGET = materialItem("copper_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> ENERGETIC_ALLOY_NUGGET = materialItem("energetic_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> VIBRANT_ALLOY_NUGGET = materialItem("vibrant_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> REDSTONE_ALLOY_NUGGET = materialItem("redstone_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> CONDUCTIVE_ALLOY_NUGGET = materialItem("conductive_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> PULSATING_ALLOY_NUGGET = materialItem("pulsating_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> DARK_STEEL_NUGGET = materialItem("dark_steel_nugget").register();
    public static final ItemEntry<MaterialItem> SOULARIUM_NUGGET = materialItem("soularium_nugget").register();
    public static final ItemEntry<MaterialItem> END_STEEL_NUGGET = materialItem("end_steel_nugget").register();

    // endregion

    // region Crafting Components

    public static final ItemEntry<MaterialItem> SILICON = materialItem("silicon").tag(EIOTags.Items.SILICON).register();

    public static final ItemEntry<MaterialItem> GRAINS_OF_INFINITY = materialItem("grains_of_infinity").lang("Grains of Infinity").register();

    public static final ItemEntry<MaterialItem> INFINITY_ROD = materialItem("infinity_rod").register();

    public static final ItemEntry<MaterialItem> CONDUIT_BINDER_COMPOSITE = materialItem("conduit_binder_composite").register();

    public static final ItemEntry<MaterialItem> CONDUIT_BINDER = materialItem("conduit_binder").register();

    public static final ItemEntry<MaterialItem> ZOMBIE_ELECTRODE = materialItem("zombie_electrode").register();

    public static final ItemEntry<MaterialItem> Z_LOGIC_CONTROLLER = materialItem("z_logic_controller").lang("Z-Logic Controller").register();

    public static final ItemEntry<MaterialItem> FRANK_N_ZOMBIE = materialItemGlinted("frank_n_zombie")
        .lang("Frank'N'Zombie")
        .model((ctx, prov) -> EIOModel.mimicItem(ctx, EIOItems.Z_LOGIC_CONTROLLER, prov))
        .register();

    public static final ItemEntry<MaterialItem> ENDER_RESONATOR = materialItem("ender_resonator").register();

    public static final ItemEntry<MaterialItem> SENTIENT_ENDER = materialItemGlinted("sentient_ender")
        .model((ctx, prov) -> EIOModel.mimicItem(ctx, EIOItems.ENDER_RESONATOR, prov))
        .register();

    public static final ItemEntry<MaterialItem> SKELETAL_CONTRACTOR = materialItem("skeletal_contractor").register();
    public static final ItemEntry<MaterialItem> GUARDIAN_DIODE = materialItem("guardian_diode").register();

    // endregion

    // region Capacitors

    public static final ItemEntry<FixedCapacitorItem> BASIC_CAPACITOR = REGISTRATE
        .item("basic_capacitor", props -> new FixedCapacitorItem(DefaultCapacitorData.BASIC, props))
        .tab(EIOCreativeTabs.MAIN)
        .register();

    public static final ItemEntry<FixedCapacitorItem> DOUBLE_LAYER_CAPACITOR = REGISTRATE
        .item("double_layer_capacitor", props -> new FixedCapacitorItem(DefaultCapacitorData.DOUBLE_LAYER, props))
        .tab(EIOCreativeTabs.MAIN)
        .register();

    public static final ItemEntry<FixedCapacitorItem> OCTADIC_CAPACITOR = REGISTRATE
        .item("octadic_capacitor", props -> new FixedCapacitorItem(DefaultCapacitorData.OCTADIC, props))
        .tab(EIOCreativeTabs.MAIN)
        .register();

    public static final ItemEntry<LootCapacitorItem> LOOT_CAPACITOR = REGISTRATE
        .item("loot_capacitor", LootCapacitorItem::new)
        .properties(p -> p.stacksTo(1))
        .register();

    // endregion

    // region Crystals

    public static final ItemEntry<MaterialItem> PULSATING_CRYSTAL = materialItemGlinted("pulsating_crystal").register();
    public static final ItemEntry<MaterialItem> VIBRANT_CRYSTAL = materialItemGlinted("vibrant_crystal").register();
    public static final ItemEntry<MaterialItem> ENDER_CRYSTAL = materialItemGlinted("ender_crystal").register();
    public static final ItemEntry<MaterialItem> ENTICING_CRYSTAL = materialItemGlinted("enticing_crystal").register();
    public static final ItemEntry<MaterialItem> WEATHER_CRYSTAL = materialItemGlinted("weather_crystal").register();
    public static final ItemEntry<MaterialItem> PRESCIENT_CRYSTAL = materialItemGlinted("prescient_crystal").register();

    // endregion

    // region Powders and Fragments

    public static final ItemEntry<MaterialItem> FLOUR = materialItem("flour").register();
    public static final ItemEntry<MaterialItem> POWDERED_COAL = materialItem("powdered_coal").tag(EIOTags.Items.DUSTS_COAL).register();
    public static final ItemEntry<MaterialItem> POWDERED_IRON = materialItem("powdered_iron").tag(EIOTags.Items.DUSTS_IRON).register();
    public static final ItemEntry<MaterialItem> POWDERED_GOLD = materialItem("powdered_gold").tag(EIOTags.Items.DUSTS_GOLD).register();
    public static final ItemEntry<MaterialItem> POWDERED_COPPER = materialItem("powdered_copper").tag(EIOTags.Items.DUSTS_COPPER).register();
    public static final ItemEntry<MaterialItem> POWDERED_TIN = materialItem("powdered_tin").tag(EIOTags.Items.DUSTS_TIN).register(); // TODO: hide if tin isn't present
    public static final ItemEntry<MaterialItem> POWDERED_ENDER_PEARL = materialItem("powdered_ender_pearl").tag(EIOTags.Items.DUSTS_ENDER).register();
    public static final ItemEntry<MaterialItem> POWDERED_OBSIDIAN = materialItem("powdered_obsidian").tag(EIOTags.Items.DUSTS_OBSIDIAN).register();
    public static final ItemEntry<MaterialItem> POWDERED_COBALT = materialItem("powdered_cobalt").tag(EIOTags.Items.DUSTS_COBALT).register(); // TODO: hide if cobalt isnt present
    public static final ItemEntry<MaterialItem> POWDERED_LAPIS_LAZULI = materialItem("powdered_lapis_lazuli").tag(EIOTags.Items.DUSTS_LAPIS).register();
    public static final ItemEntry<MaterialItem> POWDERED_QUARTZ = materialItem("powdered_quartz").tag(EIOTags.Items.DUSTS_QUARTZ).register();

    public static final ItemEntry<MaterialItem> PRESCIENT_POWDER = materialItemGlinted("prescient_powder").lang("Grains of Prescience").register();

    public static final ItemEntry<MaterialItem> VIBRANT_POWDER = materialItemGlinted("vibrant_powder").lang("Grains of Vibrancy").register();

    public static final ItemEntry<MaterialItem> PULSATING_POWDER = materialItemGlinted("pulsating_powder").lang("Grains of Piezallity").register();

    public static final ItemEntry<MaterialItem> ENDER_CRYSTAL_POWDER = materialItemGlinted("ender_crystal_powder").lang("Grains of the End").register();

    public static final ItemEntry<MaterialItem> PHOTOVOLTAIC_COMPOSITE = materialItem("photovoltaic_composite").register();
    public static final ItemEntry<MaterialItem> SOUL_POWDER = materialItem("soul_powder").register();
    public static final ItemEntry<MaterialItem> CONFUSION_POWDER = materialItem("confusing_powder").register();
    public static final ItemEntry<MaterialItem> WITHERING_POWDER = materialItem("withering_powder").register();

    // endregion

    // skipped a few

    // region Gears

    public static final ItemEntry<GearItem> GEAR_WOOD = gearItem("wood_gear", 360).lang("Wooden Gear").tag(EIOTags.Items.GEARS_WOOD).register();

    public static final ItemEntry<GearItem> GEAR_STONE = gearItem("stone_gear", 300).lang("Stone Compound Gear").tag(EIOTags.Items.GEARS_STONE).register();

    public static final ItemEntry<GearItem> GEAR_IRON = gearItem("iron_gear", 240).lang("Infinity Bimetal Gear").tag(EIOTags.Items.GEARS_IRON).register();

    public static final ItemEntry<GearItem> GEAR_ENERGIZED = gearItem("energized_gear", 180).lang("Energized Bimetal Gear").tag(EIOTags.Items.GEARS_ENERGIZED).register();

    public static final ItemEntry<GearItem> GEAR_VIBRANT = gearItem("vibrant_gear", 120).lang("Vibrant Bimetal Gear").tag(EIOTags.Items.GEARS_VIBRANT).register();

    public static final ItemEntry<GearItem> GEAR_DARK_STEEL = gearItem("dark_bimetal_gear", 60).lang("Dark Bimetal Gear").tag(EIOTags.Items.GEARS_DARK_STEEL).register();

    // endregion

    // region Dyes

    public static final ItemEntry<MaterialItem> DYE_GREEN = materialItem("organic_green_dye").tag(Tags.Items.DYES_GREEN, Tags.Items.DYES).register();

    public static final ItemEntry<MaterialItem> DYE_BROWN = materialItem("organic_brown_dye").tag(Tags.Items.DYES_BROWN, Tags.Items.DYES).register();

    public static final ItemEntry<MaterialItem> DYE_BLACK = materialItem("organic_black_dye").tag(Tags.Items.DYES_BLACK, Tags.Items.DYES).register();

    // endregion

    // region Misc Materials

    public static final ItemEntry<MaterialItem> PHOTOVOLTAIC_PLATE = materialItem("photovoltaic_plate")
        .model((ctx, prov) -> prov.withExistingParent(prov.name(ctx), prov.mcLoc("block/pressure_plate_up")).texture("texture", prov.itemTexture(ctx)))
        .register();

    public static final ItemEntry<MaterialItem> NUTRITIOUS_STICK = materialItem("nutritious_stick").register();

    public static final ItemEntry<MaterialItem> PLANT_MATTER_GREEN = materialItem("plant_matter_green").lang("Clippings and Trimmings").register();

    public static final ItemEntry<MaterialItem> PLANT_MATTER_BROWN = materialItem("plant_matter_brown").lang("Twigs and Prunings").register();

    public static final ItemEntry<MaterialItem> GLIDER_WING = materialItem("glider_wing").register();

    public static final ItemEntry<MaterialItem> ANIMAL_TOKEN = materialItemGlinted("animal_token").register();
    public static final ItemEntry<MaterialItem> MONSTER_TOKEN = materialItemGlinted("monster_token").register();
    public static final ItemEntry<MaterialItem> PLAYER_TOKEN = materialItemGlinted("player_token").register();
    public static final ItemEntry<MaterialItem> CAKE_BASE = materialItem("cake_base").register();
    public static final ItemEntry<MaterialItem> BLACK_PAPER = materialItem("black_paper").register();
    public static final ItemEntry<MaterialItem> CLAYED_GLOWSTONE = materialItem("clayed_glowstone").register();
    public static final ItemEntry<MaterialItem> NETHERCOTTA = materialItem("nethercotta").register();
    public static final ItemEntry<MaterialItem> REDSTONE_FILTER_BASE = materialItem("redstone_filter_base").register();

    public static final ItemEntry<BrokenSpawnerItem> BROKEN_SPAWNER = REGISTRATE
        .item("broken_spawner", BrokenSpawnerItem::new)
        .model(EIOModel::fakeBlockModel)
        .tab(EIOCreativeTabs.MAIN)
        .tab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(BrokenSpawnerItem.gePossibleStacks()))
        .register();

    // endregion

    // region GrindingBalls

    public static final ItemEntry<MaterialItem> SOULARIUM_BALL = materialItem("soularium_grinding_ball").register();
    public static final ItemEntry<MaterialItem> CONDUCTIVE_ALLOY_BALL = materialItem("conductive_alloy_grinding_ball").register();
    public static final ItemEntry<MaterialItem> PULSATING_ALLOY_BALL = materialItem("pulsating_alloy_grinding_ball").register();
    public static final ItemEntry<MaterialItem> REDSTONE_ALLOY_BALL = materialItem("redstone_alloy_grinding_ball").register();
    public static final ItemEntry<MaterialItem> ENERGETIC_ALLOY_BALL = materialItem("energetic_alloy_grinding_ball").register();
    public static final ItemEntry<MaterialItem> VIBRANT_ALLOY_BALL = materialItem("vibrant_alloy_grinding_ball").register();
    public static final ItemEntry<MaterialItem> COPPER_ALLOY_BALL = materialItem("copper_alloy_grinding_ball").register();
    public static final ItemEntry<MaterialItem> DARK_STEEL_BALL = materialItem("dark_steel_grinding_ball").register();
    public static final ItemEntry<MaterialItem> END_STEEL_BALL = materialItem("end_steel_grinding_ball").register();
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

    private static ItemBuilder<HangGliderItem, Registrate> gliderItem(String name) {
        return dumbItem(name, HangGliderItem::new)
            .tag(EIOTags.Items.GLIDER)
            .tab(EIOCreativeTabs.MAIN)
            .model((ctx, prov) -> GliderItemModel.create(ctx.get(), prov));
    }

    private static ItemBuilder<MaterialItem, Registrate> materialItem(String name) {
        return REGISTRATE.item(name, props -> new MaterialItem(props, false)).tab(EIOCreativeTabs.MAIN);
    }

    private static ItemBuilder<GearItem, Registrate> gearItem(String name, float tpr) {
        return REGISTRATE
            .item(name, props -> new GearItem(props, tpr))
            .model((ctx, cons) -> RotatingItemModel.create(ctx.get(), cons))
            .tab(EIOCreativeTabs.MAIN);
    }

    //  private static ItemBuilder<MaterialItem, Registrate> dependMaterialItem(String name, Tag<Item> dependency) {
    //    return REGISTRATE.item(name, props -> new MaterialItem(props, false, dependency))
    //        .group(() -> EnderIO.TAB_MAIN));
    //  }

    private static ItemBuilder<MaterialItem, Registrate> materialItemGlinted(String name) {
        return REGISTRATE.item(name, props -> new MaterialItem(props, true)).tab(EIOCreativeTabs.MAIN);
    }

    // endregion

    // region Items

    // TODO: Will need sorted once we have added more.

    public static final ItemEntry<SoulVialItem> EMPTY_SOUL_VIAL = groupedItem("empty_soul_vial", SoulVialItem::new, EIOCreativeTabs.SOULS);

    public static final ItemEntry<SoulVialItem> FILLED_SOUL_VIAL = REGISTRATE
        .item("filled_soul_vial", SoulVialItem::new)
        .properties(props -> props.stacksTo(1))
        .tab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(SoulVialItem.getAllFilled()))
        .removeTab(CreativeModeTabs.SEARCH)
        .register();

    public static final ItemEntry<EnderiosItem> ENDERIOS = REGISTRATE
        .item("enderios", EnderiosItem::new)
        .tab(EIOCreativeTabs.MAIN)
        .lang("\"Enderios\"")
        .properties(props -> props.stacksTo(1))
        .register();

    // endregion

    // region Tools
    public static final ItemEntry<YetaWrenchItem> YETA_WRENCH = REGISTRATE
        .item("yeta_wrench", YetaWrenchItem::new)
        .tab(EIOCreativeTabs.GEAR)
        .properties(props -> props.stacksTo(1))
        .tag(EIOTags.Items.WRENCH)
        .register();

    public static final ItemEntry<LocationPrintoutItem> LOCATION_PRINTOUT = REGISTRATE
        .item("location_printout", LocationPrintoutItem::new)
        .tab(EIOCreativeTabs.GEAR)
        .properties(props -> props.stacksTo(1))
        .register();

    public static final ItemEntry<CoordinateSelectorItem> COORDINATE_SELECTOR = REGISTRATE
        .item("coordinate_selector", CoordinateSelectorItem::new)
        .tab(EIOCreativeTabs.GEAR)
        .properties(props -> props.stacksTo(1))
        .register();

    public static final ItemEntry<ExperienceRodItem> EXPERIENCE_ROD = REGISTRATE
        .item("experience_rod", ExperienceRodItem::new)
        .tab(EIOCreativeTabs.GEAR)
        .register();

    public static final ItemEntry<LevitationStaffItem> LEVITATION_STAFF = REGISTRATE
        .item("staff_of_levity", LevitationStaffItem::new)
        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.LEVITATION_STAFF.get().addAllVariants(modifier))
        .register();

    public static final ItemEntry<ElectromagnetItem> ELECTROMAGNET = REGISTRATE
        .item("electromagnet", ElectromagnetItem::new)
        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.ELECTROMAGNET.get().addAllVariants(modifier))
        .register();

    public static final ItemEntry<ColdFireIgniter> COLD_FIRE_IGNITER = REGISTRATE
        .item("cold_fire_igniter", ColdFireIgniter::new)
        .defaultModel()
        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.COLD_FIRE_IGNITER.get().addAllVariants(modifier)) // TODO: Might PR this to Registrate so its nicer, but I like the footprint.
        .register();

    // endregion

    // region DarkSteel

    public static final Tier DARK_STEEL_TIER = TierSortingRegistry.registerTier(
        new ForgeTier(3, 2000, 8.0F, 3, 25, EIOTags.Blocks.DARK_STEEL_TIER, () -> Ingredient.of(EIOItems.DARK_STEEL_INGOT.get())),
        EnderIO.loc("dark_steel_tier"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));

    public static final ItemEntry<DarkSteelSwordItem> DARK_STEEL_SWORD = REGISTRATE
        .item("dark_steel_sword", DarkSteelSwordItem::new)
        .properties(p -> p.durability(2000))
        .tab(EIOCreativeTabs.GEAR)
        .lang("The Ender")
        .model((ctx, prov) -> prov.handheld(ctx))
        .register();

    // TODO: Bring these back when they are finished.
//    public static final ItemEntry<DarkSteelPickaxeItem> DARK_STEEL_PICKAXE = REGISTRATE
//        .item("dark_steel_pickaxe", DarkSteelPickaxeItem::new)
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_PICKAXE.get().addAllVariants(modifier))
//        .onRegister(item -> DarkSteelUpgradeRegistry
//            .instance()
//            .addUpgradesForItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), EmpoweredUpgrade.NAME, SpoonUpgrade.NAME, DirectUpgrade.NAME,
//                ExplosiveUpgrade.NAME, ExplosivePenetrationUpgrade.NAME))
//        .register();
//
//    public static final ItemEntry<DarkSteelAxeItem> DARK_STEEL_AXE = REGISTRATE
//        .item("dark_steel_axe", DarkSteelAxeItem::new)
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_AXE.get().addAllVariants(modifier))
//        .onRegister(item -> DarkSteelUpgradeRegistry
//            .instance()
//            .addUpgradesForItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), EmpoweredUpgrade.NAME, ForkUpgrade.NAME, DirectUpgrade.NAME))
//        .register();
//
//    private static final String UPGRADE_TEXT = " Upgrade";
//
//    public static final ItemEntry<MaterialItem> DARK_STEEL_UPGRADE_BLANK = REGISTRATE
//        .item("dark_steel_upgrade_blank", props -> new MaterialItem(props, false))
//        .tab(EIOCreativeTabs.GEAR)
//        .lang("Blank" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_1 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_1",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.ONE.getActivationCost(), EmpoweredUpgradeTier.ONE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_1.get().addAllVariants(modifier))
//        .lang("Empowered" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_2 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_2",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.TWO.getActivationCost(), EmpoweredUpgradeTier.TWO.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_2.get().addAllVariants(modifier))
//        .lang("Empowered II" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_3 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_3",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.THREE.getActivationCost(), EmpoweredUpgradeTier.THREE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_3.get().addAllVariants(modifier))
//        .lang("Empowered III" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_4 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_4",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.FOUR.getActivationCost(), EmpoweredUpgradeTier.FOUR.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_4.get().addAllVariants(modifier))
//        .lang("Empowered IV" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SPOON = REGISTRATE
//        .item("dark_steel_upgrade_spoon", properties -> new DarkSteelUpgradeItem(properties, BaseConfig.COMMON.DARK_STEEL.SPOON_ACTIVATION_COST, SpoonUpgrade::new))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_SPOON.get().addAllVariants(modifier))
//        .lang("Spoon" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_FORK = REGISTRATE
//        .item("dark_steel_upgrade_fork", properties -> new DarkSteelUpgradeItem(properties, BaseConfig.COMMON.DARK_STEEL.FORK_ACTIVATION_COST, ForkUpgrade::new))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_FORK.get().addAllVariants(modifier))
//        .lang("Fork" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_DIRECT = REGISTRATE
//        .item("dark_steel_upgrade_direct",
//            properties -> new DarkSteelUpgradeItem(properties, BaseConfig.COMMON.DARK_STEEL.DIRECT_ACTIVATION_COST, DirectUpgrade::new))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_DIRECT.get().addAllVariants(modifier))
//        .lang("Direct" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_1 = REGISTRATE
//        .item("dark_steel_upgrade_tnt", properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.ONE.getActivationCost(),
//            ExplosiveUpgradeTier.ONE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_1.get().addAllVariants(modifier))
//        .lang("Explosive I" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_2 = REGISTRATE
//        .item("dark_steel_upgrade_tnt1", properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.TWO.getActivationCost(),
//            ExplosiveUpgradeTier.TWO.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_2.get().addAllVariants(modifier))
//        .lang("Explosive II" + UPGRADE_TEXT)
//        .register();
//
//    //TODO: Textures for dark_steel_upgrade_penetration_1 and dark_steel_upgrade_penetration_2 needed
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1 = REGISTRATE
//        .item("dark_steel_upgrade_penetration_1", properties -> new DarkSteelUpgradeItem(properties, ExplosivePenetrationUpgradeTier.ONE.getActivationCost(),
//            ExplosivePenetrationUpgradeTier.ONE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1.get().addAllVariants(modifier))
//        .lang("Explosive Penetration I" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2 = REGISTRATE
//        .item("dark_steel_upgrade_penetration_2", properties -> new DarkSteelUpgradeItem(properties, ExplosivePenetrationUpgradeTier.TWO.getActivationCost(),
//            ExplosivePenetrationUpgradeTier.TWO.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2.get().addAllVariants(modifier))
//        .lang("Explosive Penetration II" + UPGRADE_TEXT)
//        .register();

    // endregion

    // region description

    public static MutableComponent capacitorDescriptionBuilder(String type, String value, String description) {
        return REGISTRATE.addLang("description", EnderIO.loc("capacitor." + type + "." + value), description);
    }

    // endregion

    // region Creative Tab Icons

    public static final ItemEntry<CreativeTabIconItem> CREATIVE_ICON_NONE = dumbItem("enderface_none", CreativeTabIconItem::new).register();
    public static final ItemEntry<CreativeTabIconItem> CREATIVE_ICON_ITEMS = dumbItem("enderface_items", CreativeTabIconItem::new).register();
    public static final ItemEntry<CreativeTabIconItem> CREATIVE_ICON_MATERIALS = dumbItem("enderface_materials", CreativeTabIconItem::new).register();
    public static final ItemEntry<CreativeTabIconItem> CREATIVE_ICON_MACHINES = dumbItem("enderface_machines", CreativeTabIconItem::new).register();
    public static final ItemEntry<CreativeTabIconItem> CREATIVE_ICON_CONDUITS = dumbItem("enderface_conduits", CreativeTabIconItem::new).register();
    public static final ItemEntry<CreativeTabIconItem> CREATIVE_ICON_MOBS = dumbItem("enderface_mobs", CreativeTabIconItem::new).register();
    public static final ItemEntry<CreativeTabIconItem> CREATIVE_ICON_INVPANEL = dumbItem("enderface_invpanel", CreativeTabIconItem::new).register();

    // endregion

    // region Helpers

    public static <T extends Item> ItemBuilder<T, Registrate> dumbItem(String name, NonNullFunction<Item.Properties, T> factory) {
        return REGISTRATE.item(name, factory).removeTab(CreativeModeTabs.SEARCH);
    }
    public static ItemBuilder<Item, Registrate> dumbItem(String name) {
        return REGISTRATE.item(name, Item::new);
    }

    public static <T extends Item> ItemEntry<T> groupedItem(String name, NonNullFunction<Item.Properties, T> factory, ResourceKey<CreativeModeTab> tab) {
        return REGISTRATE.item(name, factory).tab(tab).register();
    }

    // endregion

    public static void register() {}
}
