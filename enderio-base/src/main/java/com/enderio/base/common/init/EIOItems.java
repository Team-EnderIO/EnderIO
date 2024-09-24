package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.api.capacitor.CapacitorData;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.common.capability.EntityFilterCapability;
import com.enderio.base.common.capability.FluidFilterCapability;
import com.enderio.base.common.capability.ItemFilterCapability;
import com.enderio.base.common.item.capacitors.CapacitorItem;
import com.enderio.base.common.item.capacitors.LootCapacitorItem;
import com.enderio.base.common.item.filter.EntityFilter;
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
import com.enderio.core.data.model.ModelHelper;
import com.enderio.regilite.items.ItemBuilder;
import com.enderio.regilite.items.RegiliteItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Function;

@SuppressWarnings("unused")
public class EIOItems {
    private static final RegiliteItems ITEMS = EnderIOBase.REGILITE.items();

    // region Alloys

    public static final DeferredItem<MaterialItem> COPPER_ALLOY_INGOT = materialItem("copper_alloy_ingot").tags(EIOTags.Items.INGOTS_COPPER_ALLOY).finish();
    public static final DeferredItem<MaterialItem> ENERGETIC_ALLOY_INGOT = materialItem("energetic_alloy_ingot").tags(EIOTags.Items.INGOTS_ENERGETIC_ALLOY).finish();
    public static final DeferredItem<MaterialItem> VIBRANT_ALLOY_INGOT = materialItem("vibrant_alloy_ingot").tags(EIOTags.Items.INGOTS_VIBRANT_ALLOY).finish();
    public static final DeferredItem<MaterialItem> REDSTONE_ALLOY_INGOT = materialItem("redstone_alloy_ingot").tags(EIOTags.Items.INGOTS_REDSTONE_ALLOY).finish();
    public static final DeferredItem<MaterialItem> CONDUCTIVE_ALLOY_INGOT = materialItem("conductive_alloy_ingot").tags(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY).finish();
    public static final DeferredItem<MaterialItem> PULSATING_ALLOY_INGOT = materialItem("pulsating_alloy_ingot").tags(EIOTags.Items.INGOTS_PULSATING_ALLOY).finish();
    public static final DeferredItem<MaterialItem> DARK_STEEL_INGOT = materialItem("dark_steel_ingot").tags(EIOTags.Items.INGOTS_DARK_STEEL).finish();
    public static final DeferredItem<MaterialItem> SOULARIUM_INGOT = materialItem("soularium_ingot").tags(EIOTags.Items.INGOTS_SOULARIUM).finish();
    public static final DeferredItem<MaterialItem> END_STEEL_INGOT = materialItem("end_steel_ingot").tags(EIOTags.Items.INGOTS_END_STEEL).finish();

    public static final DeferredItem<MaterialItem> COPPER_ALLOY_NUGGET = materialItem("copper_alloy_nugget").tags(EIOTags.Items.NUGGETS_COPPER_ALLOY).finish();
    public static final DeferredItem<MaterialItem> ENERGETIC_ALLOY_NUGGET = materialItem("energetic_alloy_nugget").tags(EIOTags.Items.NUGGETS_ENERGETIC_ALLOY).finish();
    public static final DeferredItem<MaterialItem> VIBRANT_ALLOY_NUGGET = materialItem("vibrant_alloy_nugget").tags(EIOTags.Items.NUGGETS_VIBRANT_ALLOY).finish();
    public static final DeferredItem<MaterialItem> REDSTONE_ALLOY_NUGGET = materialItem("redstone_alloy_nugget").tags(EIOTags.Items.NUGGETS_REDSTONE_ALLOY).finish();
    public static final DeferredItem<MaterialItem> CONDUCTIVE_ALLOY_NUGGET = materialItem("conductive_alloy_nugget").tags(EIOTags.Items.NUGGETS_CONDUCTIVE_ALLOY).finish();
    public static final DeferredItem<MaterialItem> PULSATING_ALLOY_NUGGET = materialItem("pulsating_alloy_nugget").tags(EIOTags.Items.NUGGETS_PULSATING_ALLOY).finish();
    public static final DeferredItem<MaterialItem> DARK_STEEL_NUGGET = materialItem("dark_steel_nugget").tags(EIOTags.Items.NUGGETS_DARK_STEEL).finish();
    public static final DeferredItem<MaterialItem> SOULARIUM_NUGGET = materialItem("soularium_nugget").tags(EIOTags.Items.NUGGETS_SOULARIUM).finish();
    public static final DeferredItem<MaterialItem> END_STEEL_NUGGET = materialItem("end_steel_nugget").tags(EIOTags.Items.NUGGETS_END_STEEL).finish();

    // endregion

    // region Crafting Components

    public static final DeferredItem<MaterialItem> SILICON = materialItem("silicon")
        .tags(EIOTags.Items.SILICON)
        .finish();

    public static final DeferredItem<MaterialItem> GRAINS_OF_INFINITY = materialItem("grains_of_infinity")
        .tags(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
        .translation("Grains of Infinity")
        .finish();

    public static final DeferredItem<MaterialItem> INFINITY_ROD = materialItem("infinity_rod").finish();

    public static final DeferredItem<MaterialItem> CONDUIT_BINDER_COMPOSITE = materialItem("conduit_binder_composite").finish();

    public static final DeferredItem<MaterialItem> CONDUIT_BINDER = materialItem("conduit_binder").finish();

    public static final DeferredItem<MaterialItem> ZOMBIE_ELECTRODE = materialItem("zombie_electrode").finish();

    public static final DeferredItem<MaterialItem> Z_LOGIC_CONTROLLER = materialItem("z_logic_controller")
        .translation("Z-Logic Controller")
        .finish();

    public static final DeferredItem<MaterialItem> FRANK_N_ZOMBIE = materialItemGlinted("frank_n_zombie")
        .translation("Frank'N'Zombie")
        .modelProvider((prov, ctx) -> ModelHelper.mimicItem(prov, ctx, EIOItems.Z_LOGIC_CONTROLLER))
        .finish();

    public static final DeferredItem<MaterialItem> ENDER_RESONATOR = materialItem("ender_resonator").finish();

    public static final DeferredItem<MaterialItem> SENTIENT_ENDER = materialItemGlinted("sentient_ender")
        .modelProvider((prov, ctx) -> ModelHelper.mimicItem(prov, ctx, EIOItems.ENDER_RESONATOR))
        .finish();

    public static final DeferredItem<MaterialItem> SKELETAL_CONTRACTOR = materialItem("skeletal_contractor").finish();
    public static final DeferredItem<MaterialItem> GUARDIAN_DIODE = materialItem("guardian_diode").finish();

    // endregion

    // region Capacitors

    public static final DeferredItem<CapacitorItem> BASIC_CAPACITOR = ITEMS
        .create("basic_capacitor",
            props -> new CapacitorItem(
                props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(1))),
            new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.MAIN)
        .finish();

    public static final DeferredItem<CapacitorItem> DOUBLE_LAYER_CAPACITOR = ITEMS
        .create("double_layer_capacitor",
            props -> new CapacitorItem(
                props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(2))),
            new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.MAIN)
        .finish();

    public static final DeferredItem<CapacitorItem> OCTADIC_CAPACITOR = ITEMS
        .create("octadic_capacitor",
            props -> new CapacitorItem(
                props.component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(3))),
            new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.MAIN)
        .finish();

    public static final DeferredItem<LootCapacitorItem> LOOT_CAPACITOR = ITEMS
        .create("loot_capacitor", LootCapacitorItem::new, new Item.Properties()
            .stacksTo(1))
        .finish();

    // endregion

    // region Crystals

    public static final DeferredItem<MaterialItem> PULSATING_CRYSTAL = materialItemGlinted("pulsating_crystal")
        .tags(EIOTags.Items.GEMS_PULSATING_CRYSTAL)
        .finish();

    public static final DeferredItem<MaterialItem> VIBRANT_CRYSTAL = materialItemGlinted("vibrant_crystal")
        .tags(EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
        .finish();

    public static final DeferredItem<MaterialItem> ENDER_CRYSTAL = materialItemGlinted("ender_crystal")
        .tags(EIOTags.Items.GEMS_ENDER_CRYSTAL)
        .finish();

    public static final DeferredItem<MaterialItem> ENTICING_CRYSTAL = materialItemGlinted("enticing_crystal")
        .tags(EIOTags.Items.GEMS_ENTICING_CRYSTAL)
        .finish();

    public static final DeferredItem<MaterialItem> WEATHER_CRYSTAL = materialItemGlinted("weather_crystal")
        .tags(EIOTags.Items.GEMS_WEATHER_CRYSTAL)
        .finish();

    public static final DeferredItem<MaterialItem> PRESCIENT_CRYSTAL = materialItemGlinted("prescient_crystal")
        .tags(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL)
        .finish();

    // endregion

    // region Powders and Fragments

    public static final DeferredItem<MaterialItem> FLOUR = materialItem("flour").finish();
    public static final DeferredItem<MaterialItem> POWDERED_COAL = materialItem("powdered_coal")
        .tags(EIOTags.Items.DUSTS_COAL)
        .finish();

    public static final DeferredItem<MaterialItem> POWDERED_IRON = materialItem("powdered_iron")
        .tags(EIOTags.Items.DUSTS_IRON)
        .finish();

    public static final DeferredItem<MaterialItem> POWDERED_GOLD = materialItem("powdered_gold")
        .tags(EIOTags.Items.DUSTS_GOLD)
        .finish();

    public static final DeferredItem<MaterialItem> POWDERED_COPPER = materialItem("powdered_copper")
        .tags(EIOTags.Items.DUSTS_COPPER)
        .finish();

    public static final DeferredItem<MaterialItem> POWDERED_TIN = materialItem("powdered_tin")
        .tags(EIOTags.Items.DUSTS_TIN)
        .finish(); // TODO: hide if tin isn't present

    public static final DeferredItem<MaterialItem> POWDERED_ENDER_PEARL = materialItem("powdered_ender_pearl")
        .tags(EIOTags.Items.DUSTS_ENDER)
        .finish();

    public static final DeferredItem<MaterialItem> POWDERED_OBSIDIAN = materialItem("powdered_obsidian")
        .tags(EIOTags.Items.DUSTS_OBSIDIAN)
        .finish();

    public static final DeferredItem<MaterialItem> POWDERED_COBALT = materialItem("powdered_cobalt")
        .tags(EIOTags.Items.DUSTS_COBALT)
        .finish(); // TODO: hide if cobalt isnt present

    public static final DeferredItem<MaterialItem> POWDERED_LAPIS_LAZULI = materialItem("powdered_lapis_lazuli")
        .tags(EIOTags.Items.DUSTS_LAPIS)
        .finish();

    public static final DeferredItem<MaterialItem> POWDERED_QUARTZ = materialItem("powdered_quartz")
        .tags(EIOTags.Items.DUSTS_QUARTZ)
        .finish();

    public static final DeferredItem<MaterialItem> PRESCIENT_POWDER = materialItemGlinted("prescient_powder")
        .tags(EIOTags.Items.DUSTS_GRAINS_OF_PRESCIENCE)
        .translation("Grains of Prescience")
        .finish();

    public static final DeferredItem<MaterialItem> VIBRANT_POWDER = materialItemGlinted("vibrant_powder")
        .tags(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY)
        .translation("Grains of Vibrancy")
        .finish();

    public static final DeferredItem<MaterialItem> PULSATING_POWDER = materialItemGlinted("pulsating_powder")
        .tags(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY)
        .translation("Grains of Piezallity")
        .finish();

    public static final DeferredItem<MaterialItem> ENDER_CRYSTAL_POWDER = materialItemGlinted("ender_crystal_powder")
        .tags(EIOTags.Items.DUSTS_GRAINS_OF_THE_END)
        .translation("Grains of the End")
        .finish();

    public static final DeferredItem<MaterialItem> PHOTOVOLTAIC_COMPOSITE = materialItem("photovoltaic_composite").finish();
    public static final DeferredItem<MaterialItem> SOUL_POWDER = materialItem("soul_powder").finish();
    public static final DeferredItem<MaterialItem> CONFUSION_POWDER = materialItem("confusing_powder").finish();
    public static final DeferredItem<MaterialItem> WITHERING_POWDER = materialItem("withering_powder").finish();

    // endregion

    // skipped a few

    // region Gears

    public static final DeferredItem<MaterialItem> GEAR_WOOD = materialItem("wood_gear")
        .translation("Wooden Gear")
        .tags(EIOTags.Items.GEARS_WOOD)
        .finish();

    public static final DeferredItem<MaterialItem> GEAR_STONE = materialItem("stone_gear")
        .translation("Stone Compound Gear")
        .tags(EIOTags.Items.GEARS_STONE)
        .finish();

    public static final DeferredItem<MaterialItem> GEAR_IRON = materialItem("iron_gear")
        .translation("Infinity Bimetal Gear")
        .tags(EIOTags.Items.GEARS_IRON)
        .finish();

    public static final DeferredItem<MaterialItem> GEAR_ENERGIZED = materialItem("energized_gear")
        .translation("Energized Bimetal Gear")
        .tags(EIOTags.Items.GEARS_ENERGIZED)
        .finish();

    public static final DeferredItem<MaterialItem> GEAR_VIBRANT = materialItem("vibrant_gear")
        .translation("Vibrant Bimetal Gear")
        .tags(EIOTags.Items.GEARS_VIBRANT)
        .finish();

    public static final DeferredItem<MaterialItem> GEAR_DARK_STEEL = materialItem("dark_bimetal_gear")
        .translation("Dark Bimetal Gear")
        .tags(EIOTags.Items.GEARS_DARK_STEEL)
        .finish();

    // endregion

    // region Dyes

    public static final DeferredItem<MaterialItem> DYE_GREEN = materialItem("organic_green_dye")
        .tags(Tags.Items.DYES_GREEN, Tags.Items.DYES)
        .finish();

    public static final DeferredItem<MaterialItem> DYE_BROWN = materialItem("organic_brown_dye")
        .tags(Tags.Items.DYES_BROWN, Tags.Items.DYES)
        .finish();

    public static final DeferredItem<MaterialItem> DYE_BLACK = materialItem("organic_black_dye")
        .tags(Tags.Items.DYES_BLACK, Tags.Items.DYES)
        .finish();

    // endregion

    // region Misc Materials

    public static final DeferredItem<MaterialItem> PHOTOVOLTAIC_PLATE = materialItem("photovoltaic_plate").finish();

    public static final DeferredItem<MaterialItem> NUTRITIOUS_STICK = materialItem("nutritious_stick").finish();

    public static final DeferredItem<MaterialItem> PLANT_MATTER_GREEN = materialItem("plant_matter_green")
        .translation("Clippings and Trimmings")
        .finish();

    public static final DeferredItem<MaterialItem> PLANT_MATTER_BROWN = materialItem("plant_matter_brown")
        .translation("Twigs and Prunings")
        .finish();

    public static final DeferredItem<MaterialItem> GLIDER_WING = materialItem("glider_wing").finish();

    public static final DeferredItem<MaterialItem> ANIMAL_TOKEN = materialItemGlinted("animal_token").finish();
    public static final DeferredItem<MaterialItem> MONSTER_TOKEN = materialItemGlinted("monster_token").finish();
    public static final DeferredItem<MaterialItem> PLAYER_TOKEN = materialItemGlinted("player_token").finish();
    public static final DeferredItem<MaterialItem> CAKE_BASE = materialItem("cake_base").finish();
    public static final DeferredItem<MaterialItem> BLACK_PAPER = materialItem("black_paper").finish();
    public static final DeferredItem<MaterialItem> CLAYED_GLOWSTONE = materialItem("clayed_glowstone").finish();
    public static final DeferredItem<MaterialItem> NETHERCOTTA = materialItem("nethercotta").finish();
    public static final DeferredItem<MaterialItem> REDSTONE_FILTER_BASE = materialItem("redstone_filter_base").finish();

    public static final DeferredItem<BrokenSpawnerItem> BROKEN_SPAWNER = ITEMS
        .create("broken_spawner", BrokenSpawnerItem::new)
        .tags(EIOTags.Items.ENTITY_STORAGE)
        .modelProvider(ModelHelper::fakeBlockModel)
        .tab(EIOCreativeTabs.MAIN)
        .tab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(BrokenSpawnerItem.getPossibleStacks()))
        .finish();

    // endregion

    // region Grinding Balls

    // TODO: 20.6: Config for grinding balls?

    public static final DeferredItem<MaterialItem> SOULARIUM_BALL = grindingBall("soularium_grinding_ball",
        new GrindingBallData(1.2F, 2.15F, 0.9F, 80000));

    public static final DeferredItem<MaterialItem> CONDUCTIVE_ALLOY_BALL = grindingBall("conductive_alloy_grinding_ball",
        new GrindingBallData(1.35F, 1.00F, 1.0F, 40000));

    public static final DeferredItem<MaterialItem> PULSATING_ALLOY_BALL = grindingBall("pulsating_alloy_grinding_ball",
        new GrindingBallData(1.00F, 1.85F, 1.0F, 100000));

    public static final DeferredItem<MaterialItem> REDSTONE_ALLOY_BALL = grindingBall("redstone_alloy_grinding_ball",
        new GrindingBallData(1.00F, 1.00F, 0.35F, 30000));

    public static final DeferredItem<MaterialItem> ENERGETIC_ALLOY_BALL = grindingBall("energetic_alloy_grinding_ball",
        new GrindingBallData(1.6F, 1.1F, 1.1F, 80000));

    public static final DeferredItem<MaterialItem> VIBRANT_ALLOY_BALL = grindingBall("vibrant_alloy_grinding_ball",
        new GrindingBallData(1.75F, 1.35F, 1.13F, 80000));

    public static final DeferredItem<MaterialItem> COPPER_ALLOY_BALL = grindingBall("copper_alloy_grinding_ball",
        new GrindingBallData(1.2F, 1.65F, 0.8F, 40000));

    public static final DeferredItem<MaterialItem> DARK_STEEL_BALL = grindingBall("dark_steel_grinding_ball",
        new GrindingBallData(1.35F, 2.00F, 0.7F, 125000));

    public static final DeferredItem<MaterialItem> END_STEEL_BALL = grindingBall("end_steel_grinding_ball",
        new GrindingBallData(1.4F, 2.4F, 0.7F, 75000));

    //    public static final Map<DyeColor, DeferredItem<HangGliderItem>> COLORED_HANG_GLIDERS = Util.make(() -> {
    //       Map<DyeColor, DeferredItem<HangGliderItem>> tempMap = new EnumMap<>(DyeColor.class);
    //       for (DyeColor color: DyeColor.values()) {
    //           var entry = gliderItem(color.getName() + "_glider");
    //           tempMap.put(color, entry);
    //       }
    //       return tempMap;
    //    });

    //    public static final DeferredItem<HangGliderItem> GLIDER = gliderItem("glider");

    private static DeferredItem<MaterialItem> grindingBall(String name, GrindingBallData grindingBallData) {
        return ITEMS
            .create(name, props -> new MaterialItem(
                props
                    .component(EIODataComponents.GRINDING_BALL, grindingBallData),
                false))
            .tags(EIOTags.Items.GRINDING_BALLS)
            .tab(EIOCreativeTabs.MAIN)
            .finish();
    }

    // endregion

    // region Builders

    private static DeferredItem<HangGliderItem> gliderItem(String name) {
        return dumbItem(name, HangGliderItem::new)
            .tags(EIOTags.Items.GLIDER)
            .tab(EIOCreativeTabs.MAIN)
            .modelProvider((prov, ctx) -> GliderItemModel.create(ctx.get(), prov))
            .finish();
    }

    private static ItemBuilder<MaterialItem> materialItem(String name) {
        return ITEMS
            .create(name, props -> new MaterialItem(props, false))
            .tab(EIOCreativeTabs.MAIN);
    }

    private static ItemBuilder<MaterialItem> materialItemGlinted(String name) {
        return ITEMS
            .create(name, props -> new MaterialItem(props, true))
            .tab(EIOCreativeTabs.MAIN);
    }

    // endregion

    // region Items

    public static final DeferredItem<SoulVialItem> EMPTY_SOUL_VIAL = groupedItem("empty_soul_vial", SoulVialItem::new, EIOCreativeTabs.SOULS).finish();

    public static final DeferredItem<SoulVialItem> FILLED_SOUL_VIAL = ITEMS
        .create("filled_soul_vial", SoulVialItem::new, new Item.Properties().stacksTo(1))
        .tags(EIOTags.Items.ENTITY_STORAGE)
        .tab(EIOCreativeTabs.SOULS, modifier -> modifier.acceptAll(SoulVialItem.getAllFilled(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY))
        .finish();

    public static final DeferredItem<EnderiosItem> ENDERIOS = ITEMS
        .create("enderios", EnderiosItem::new, new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.MAIN)
        .translation("\"Enderios\"")
        .finish();

    // endregion

    // region Tools
    public static final DeferredItem<YetaWrenchItem> YETA_WRENCH = ITEMS
        .create("yeta_wrench", YetaWrenchItem::new, new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.GEAR)
        .tags(EIOTags.Items.WRENCH)
        .finish();

    public static final DeferredItem<LocationPrintoutItem> LOCATION_PRINTOUT = ITEMS
        .create("location_printout", LocationPrintoutItem::new, new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.GEAR)
        .finish();

    public static final DeferredItem<CoordinateSelectorItem> COORDINATE_SELECTOR = ITEMS
        .create("coordinate_selector", CoordinateSelectorItem::new, new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.GEAR)
        .finish();

    public static final DeferredItem<ExperienceRodItem> EXPERIENCE_ROD = ITEMS
        .create("experience_rod", ExperienceRodItem::new)
        .tab(EIOCreativeTabs.GEAR)
        .finish();

    public static final DeferredItem<LevitationStaffItem> LEVITATION_STAFF = ITEMS
        .create("staff_of_levity", LevitationStaffItem::new)
        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.LEVITATION_STAFF.get().addAllVariants(modifier))
        .capability(Capabilities.FluidHandler.ITEM, LevitationStaffItem.FLUID_HANDLER_PROVIDER)
        .with(EIOItems::poweredToggledItemCapabilities)
        .finish();

    public static final DeferredItem<TravelStaffItem> TRAVEL_STAFF = ITEMS
        .create("staff_of_travelling", TravelStaffItem::new, new Item.Properties().stacksTo(1))
        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.TRAVEL_STAFF.get().addAllVariants(modifier))
        .capability(Capabilities.EnergyStorage.ITEM, TravelStaffItem.ENERGY_STORAGE_PROVIDER)
        .finish();

    public static final DeferredItem<ElectromagnetItem> ELECTROMAGNET = ITEMS
        .create("electromagnet", ElectromagnetItem::new)
        .tab(EIOCreativeTabs.GEAR, modifier -> EIOItems.ELECTROMAGNET.get().addAllVariants(modifier))
        .with(EIOItems::poweredToggledItemCapabilities)
        .finish();

    public static final DeferredItem<ColdFireIgniter> COLD_FIRE_IGNITER = ITEMS
        .create("cold_fire_igniter", ColdFireIgniter::new)
        .tab(EIOCreativeTabs.GEAR,
            modifier -> EIOItems.COLD_FIRE_IGNITER.get().addAllVariants(modifier)) // TODO: Might PR this to ITEM_REGISTRY so its nicer, but I like the footprint.
        .capability(Capabilities.FluidHandler.ITEM, ColdFireIgniter.FLUID_HANDLER_PROVIDER)
        .finish();

    // endregion

    // region filter

    public static final DeferredItem<ItemFilter> BASIC_ITEM_FILTER = ITEMS
        .create("basic_filter", properties -> new ItemFilter(properties.component(EIODataComponents.ITEM_FILTER, new ItemFilterCapability.Component(5))))
        .tab(EIOCreativeTabs.GEAR)
        .capability(EIOCapabilities.Filter.ITEM, ItemFilter.FILTER_PROVIDER)
        .finish();

    public static final DeferredItem<ItemFilter> ADVANCED_ITEM_FILTER = ITEMS
        .create("advanced_filter", properties -> new ItemFilter(properties.component(EIODataComponents.ITEM_FILTER, new ItemFilterCapability.Component(10))))
        .tab(EIOCreativeTabs.GEAR)
        .capability(EIOCapabilities.Filter.ITEM, ItemFilter.FILTER_PROVIDER)
        .finish();

    public static final DeferredItem<FluidFilter> BASIC_FLUID_FILTER = ITEMS
        .create("fluid_filter", properties -> new FluidFilter(properties.component(EIODataComponents.FLUID_FILTER, new FluidFilterCapability.Component(5))))
        .tab(EIOCreativeTabs.GEAR)
        .capability(EIOCapabilities.Filter.ITEM, FluidFilter.FILTER_PROVIDER)
        .finish();

    public static final DeferredItem<EntityFilter> ENTITY_FILTER = ITEMS
        .create("entity_filter", properties -> new EntityFilter(properties.component(EIODataComponents.ENTITY_FILTER, new EntityFilterCapability.Component(5))))
        .translation("Soul Filter")
        .tab(EIOCreativeTabs.GEAR)
        .capability(EIOCapabilities.Filter.ITEM, EntityFilter.ENTITY_FILTER)
        .finish();

    // endregion

    // region description

    public static MutableComponent capacitorDescriptionBuilder(String type, String value, String description) {
        // TODO: Regilite general translation support.
        //return REGISTRATE.addLang("description", EnderIO.loc("capacitor." + type + "." + value), description);
        return Component.empty();
    }

    // endregion

    // region Creative Tab Icons

    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON_NONE = dumbItem("enderface_none", CreativeTabIconItem::new).finish();
    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON_ITEMS = dumbItem("enderface_items", CreativeTabIconItem::new).finish();
    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON_MATERIALS = dumbItem("enderface_materials", CreativeTabIconItem::new).finish();
    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON_MACHINES = dumbItem("enderface_machines", CreativeTabIconItem::new).finish();
    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON_CONDUITS = dumbItem("enderface_conduits", CreativeTabIconItem::new).finish();
    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON_MOBS = dumbItem("enderface_mobs", CreativeTabIconItem::new).finish();
    public static final DeferredItem<CreativeTabIconItem> CREATIVE_ICON_INVPANEL = dumbItem("enderface_invpanel", CreativeTabIconItem::new).finish();

    // endregion

    // region Helpers

    public static <T extends Item> ItemBuilder<T> dumbItem(String name, Function<Item.Properties, T> factory) {
        return ITEMS.create(name, factory);
    }

    public static ItemBuilder<Item> dumbItem(String name) {
        return ITEMS.createSimple(name);
    }

    public static <T extends Item> ItemBuilder<T> groupedItem(String name, Function<Item.Properties, T> factory, ResourceKey<CreativeModeTab> tab) {
        return ITEMS.create(name, factory).tab(tab);
    }

    private static <T extends PoweredToggledItem> ItemBuilder<T> poweredToggledItemCapabilities(ItemBuilder<T> item) {
        return item.capability(Capabilities.EnergyStorage.ITEM, PoweredToggledItem.ENERGY_STORAGE_PROVIDER);
    }

    // endregion

    public static void register() {
    }
}
