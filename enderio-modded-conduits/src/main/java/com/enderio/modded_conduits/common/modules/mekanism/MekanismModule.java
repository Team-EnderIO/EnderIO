package com.enderio.modded_conduits.common.modules.mekanism;

import com.enderio.core.common.registries.MenuDeferredRegister;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.connection.path.PriorityConnectionPathComparator;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.init.EIOItems;
import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.common.modules.ConduitCommonModule;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduit;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduitConnectionConfig;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalTicker;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.ChemicalFilter;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilter;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilterItem;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilterMenu;
import com.enderio.modded_conduits.common.modules.mekanism.heat.HeatConduit;
import com.enderio.modded_conduits.common.modules.mekanism.heat.HeatConduitConnectionConfig;
import com.enderio.modded_conduits.common.modules.mekanism.heat.HeatTicker;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.function.Supplier;

public class MekanismModule implements ConduitCommonModule {

    public static final MekanismModule INSTANCE = new MekanismModule();

    private static final ModLoadedCondition CONDITION = new ModLoadedCondition("mekanism");

    // region Registries

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModdedConduits.MOD_ID);
    private static final MenuDeferredRegister MENUS = MenuDeferredRegister.create(ModdedConduits.MOD_ID);

    private static final DeferredRegister<ConduitType<?, ?>> CONDUIT_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_TYPE, ModdedConduits.MOD_ID);

    public static final DeferredRegister<ConnectionConfigType<?>> CONDUIT_CONNECTION_CONFIG_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE, ModdedConduits.MOD_ID);

    static {
        CONDUIT_CONNECTION_CONFIG_TYPES.register("chemical", () -> ChemicalConduitConnectionConfig.TYPE);
        CONDUIT_CONNECTION_CONFIG_TYPES.register("heat", () -> HeatConduitConnectionConfig.TYPE);
    }

    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModdedConduits.MOD_ID);

    // endregion

    public static final Supplier<ConduitType<ChemicalConduit, ChemicalConduitConnectionConfig>> TYPE_CHEMICAL = CONDUIT_TYPES.register("chemical",
        () -> ConduitType
            .builder(ChemicalConduit.CODEC, ChemicalConduitConnectionConfig.TYPE)
            .doesRequireNetworkCaches()
            .ticker(ChemicalTicker.INSTANCE, 5)
            .connectionComparerFromReference(new PriorityConnectionPathComparator(conn ->
                conn.node().getConnectionConfig(conn.connectionSide(), ChemicalConduitConnectionConfig.TYPE).insertPriority()))
            .build());

    public static final Supplier<ConduitType<HeatConduit, HeatConduitConnectionConfig>> TYPE_HEAT = CONDUIT_TYPES.register("heat",
        () -> ConduitType
            .builder(HeatConduit::new, HeatConduitConnectionConfig.TYPE)
            .doesRequireNetworkCaches()
            .ticker(HeatTicker.INSTANCE, 5)
            .build());

    public static final Supplier<DataComponentType<EnderChemicalFilter>> CHEMICAL_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("chemical_filter",
                    builder -> builder.persistent(EnderChemicalFilter.CODEC)
                            .networkSynchronized(EnderChemicalFilter.STREAM_CODEC));

    public static final DeferredItem<EnderChemicalFilterItem> BASIC_CHEMICAL_FILTER = ITEMS.registerItem(
        "basic_chemical_filter",
        props -> new EnderChemicalFilterItem(props, EnderChemicalFilterItem.Type.BASIC)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<EnderChemicalFilterMenu>> CHEMICAL_FILTER_MENU =
        MENUS.register("chemical_filter", EnderChemicalFilterItem.Type.BASIC::openMenu);

    public static class Capabilities {
        public static final ItemCapability<ChemicalFilter, Void> CHEMICAL_FILTER = ItemCapability.createVoid(ModdedConduits.id("chemical_filter"), ChemicalFilter.class);

        public static final BlockCapability<ResourceHandler<ChemicalResource>, Direction> CHEMICAL = BlockCapability.createSided(
                Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "chemical_handler"),
                ResourceHandler.asClass());
        public static final BlockCapability<IHeatHandler, Direction> HEAT = BlockCapability.createSided(
                Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "heat_handler"), IHeatHandler.class);

        public static class Item {
            public static final ItemCapability<ResourceHandler<ChemicalResource>, ItemAccess> CHEMICAL = ItemCapability.create(
                    Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "chemical_handler"),
                    ResourceHandler.asClass(), ItemAccess.class);
        }
    }

    public static final Component CHEMICAL_FILTER_SLOT_TOOLTIP = tooltip("chemical_filter_slot");

    private static final TagKey<Item> OSMIUM = ItemTags
            .create(Identifier.fromNamespaceAndPath("c", "ingots/osmium"));

    private static MutableComponent tooltip(String path) {
        return Component.translatable(Util.makeDescriptionId("tooltip", ModdedConduits.id("conduit/" + path)));
    }

    public static final ResourceKey<Conduit<?, ?>> CHEMICAL = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            ModdedConduits.id("chemical"));
    public static final ResourceKey<Conduit<?, ?>> PRESSURIZED_CHEMICAL = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, ModdedConduits.id("pressurized_chemical"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_CHEMICAL = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, ModdedConduits.id("ender_chemical"));
    public static final ResourceKey<Conduit<?, ?>> HEAT = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            ModdedConduits.id("heat"));

    @Override
    public void initialize(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        MENUS.register(modEventBus);
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_CONNECTION_CONFIG_TYPES.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addToCreativeTabs);

        if (ModList.get().isLoaded("laserio")) {
//            MekanismLaserIOCompat.init(modEventBus);
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
            Capabilities.CHEMICAL_FILTER,
            EnderChemicalFilterItem.CHEMICAL_FILTER_PROVIDER,
            BASIC_CHEMICAL_FILTER.get()
        );

        event.registerItem(
            EnderIOCapabilities.FILTER_MENU_PROVIDER,
            AbstractFilterItem.FILTER_MENU_PROVIDER,
            BASIC_CHEMICAL_FILTER.get()
        );
    }

    private void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == EnderIOAPI.MAIN_CREATIVE_TAB) {
            event.insertAfter(EIOItems.BASIC_SOUL_FILTER.get().getDefaultInstance(),
                BASIC_CHEMICAL_FILTER.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?, ?>> context) {
        context.register(HEAT, new HeatConduit(ModdedConduits.id("block/conduit/heat"), Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(HEAT))), CONDITION);
        context.register(CHEMICAL,
            new ChemicalConduit(ModdedConduits.id("block/conduit/chemical"), Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(CHEMICAL)),
            750), CONDITION);
        context.register(PRESSURIZED_CHEMICAL, new ChemicalConduit(ModdedConduits.id("block/conduit/pressurized_chemical"),
            Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(PRESSURIZED_CHEMICAL)), 2_000), CONDITION);
        context.register(ENDER_CHEMICAL, new ChemicalConduit(ModdedConduits.id("block/conduit/ender_chemical"),
            Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(ENDER_CHEMICAL)), 64_000), CONDITION);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        var mekRecipeOutput = recipeOutput.withConditions(CONDITION);

        var items = recipeOutput.lookup(Registries.ITEM);
        var conduits = recipeOutput.lookup(EnderIORegistries.Keys.CONDUIT);
        var chemicalConduit = conduits.getOrThrow(CHEMICAL);
        var pressurizedChemicalConduit = conduits.getOrThrow(PRESSURIZED_CHEMICAL);
        var enderChemicalConduit = conduits.getOrThrow(ENDER_CHEMICAL);
        var heatConduit = conduits.getOrThrow(HEAT);

        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitStackTemplate(chemicalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "basic_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, ModdedConduits.id("mek_basic_pressurized_tube").toString());

        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.BUILDING_BLOCKS,
                        ConduitApi.INSTANCE.getConduitStackTemplate(pressurizedChemicalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "advanced_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, ModdedConduits.id("mek_advanced_pressurized_tube").toString());

        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitStackTemplate(enderChemicalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "elite_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, ModdedConduits.id("mek_elite_pressurized_tube").toString());

        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.BUILDING_BLOCKS,
                        ConduitApi.INSTANCE.getConduitStackTemplate(pressurizedChemicalConduit, 8))
                .pattern("CCC")
                .pattern("CUC")
                .pattern("CCC")
                .define('C', ConduitIngredient.of(chemicalConduit))
                .define('U',
                        BuiltInRegistries.ITEM.getValue(
                                Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_infused")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, ModdedConduits.id("mek_basic_pressurized_tube_upgrade").toString());

        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitStackTemplate(enderChemicalConduit, 8))
                .pattern("CCC")
                .pattern("CUC")
                .pattern("CCC")
                .define('C', ConduitIngredient.of(pressurizedChemicalConduit))
                .define('U',
                        BuiltInRegistries.ITEM.getValue(
                                Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_reinforced")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, ModdedConduits.id("mek_advanced_pressurized_tube_upgrade").toString());

        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitStackTemplate(heatConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "advanced_thermodynamic_conductor")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, ModdedConduits.id("mek_advanced_thermodynamic_conductor").toString());

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, BASIC_CHEMICAL_FILTER.get())
                .pattern(" P ")
                .pattern("POP")
                .pattern(" P ")
                .define('P', Items.PAPER)
                .define('O', OSMIUM)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, OSMIUM).build()))
                .save(mekRecipeOutput, ModdedConduits.id("mek_chemical_filter").toString());
    }
}
