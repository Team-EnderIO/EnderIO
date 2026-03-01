package com.enderio.modded_conduits.common.modules.mekanism;

import com.enderio.core.common.registries.ItemDeferredRegister;
import com.enderio.core.common.registries.MenuDeferredRegister;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContextType;
import com.enderio.enderio.api.conduits.network.node.legacy.ConduitDataType;
import com.enderio.enderio.content.conduits.ConduitApiImpl;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.init.EIOCreativeTabs;
import com.enderio.enderio.init.EIOItems;
import com.enderio.modded_conduits.common.modules.ConduitCommonModule;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduit;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduitConnectionConfig;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduitData;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalTicker;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.ChemicalFilter;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilter;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilterItem;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilterMenu;
import com.enderio.modded_conduits.common.modules.mekanism.heat.HeatConduit;
import com.enderio.modded_conduits.common.modules.mekanism.heat.HeatConduitConnectionConfig;
import com.enderio.modded_conduits.common.modules.mekanism.heat.HeatTicker;
import com.enderio.modded_conduits.common.modules.mekanism.laserio.MekanismLaserIOCompat;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MekanismModule implements ConduitCommonModule {

    public static final MekanismModule INSTANCE = new MekanismModule();

    private static final ModLoadedCondition CONDITION = new ModLoadedCondition("mekanism");

    // region Registries

    private static final ItemDeferredRegister ITEMS = ItemDeferredRegister.create(EnderIO.MOD_ID);
    private static final MenuDeferredRegister MENUS = MenuDeferredRegister.create(EnderIO.MOD_ID);

    private static final DeferredRegister<ConduitType<?, ?>> CONDUIT_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_TYPE, EnderIO.MOD_ID);

    public static final DeferredRegister<ConduitDataType<?>> CONDUIT_DATA_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_DATA_TYPE, EnderIO.MOD_ID);

    public static final DeferredRegister<ConnectionConfigType<?>> CONDUIT_CONNECTION_CONFIG_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE, EnderIO.MOD_ID);

    public static final DeferredRegister<ConduitNetworkContextType<?>> CONDUIT_NETWORK_CONTEXT_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_TYPE, EnderIO.MOD_ID);

    static {
        CONDUIT_CONNECTION_CONFIG_TYPES.register("chemical", () -> ChemicalConduitConnectionConfig.TYPE);
        CONDUIT_CONNECTION_CONFIG_TYPES.register("heat", () -> HeatConduitConnectionConfig.TYPE);
    }

    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister
            .createDataComponents(EnderIO.MOD_ID);

    // endregion

    public static final Supplier<ConduitType<ChemicalConduit, ChemicalConduitConnectionConfig>> TYPE_CHEMICAL = CONDUIT_TYPES.register("chemical",
        () -> ConduitType
            .builder(ChemicalConduit.CODEC, ChemicalConduitConnectionConfig.TYPE)
            .doesRequireNetworkCaches()
            .ticker(ChemicalTicker.INSTANCE, 5)
            .build());

    public static final Supplier<ConduitType<HeatConduit, HeatConduitConnectionConfig>> TYPE_HEAT = CONDUIT_TYPES.register("heat",
        () -> ConduitType
            .builder(HeatConduit::new, HeatConduitConnectionConfig.TYPE)
            .doesRequireNetworkCaches()
            .ticker(HeatTicker.INSTANCE, 5)
            .build());

    public static final Supplier<ConduitDataType<ChemicalConduitData>> CHEMICAL_DATA_TYPE = CONDUIT_DATA_TYPES
            .register("chemical", () -> new ConduitDataType<>(ChemicalConduitData.CODEC,
                    ChemicalConduitData.STREAM_CODEC, ChemicalConduitData::new));

    public static final Supplier<DataComponentType<EnderChemicalFilter>> CHEMICAL_FILTER = DATA_COMPONENT_TYPES
            .registerComponentType("chemical_filter",
                    builder -> builder.persistent(EnderChemicalFilter.CODEC)
                            .networkSynchronized(EnderChemicalFilter.STREAM_CODEC));

    public static final DeferredItem<EnderChemicalFilterItem> BASIC_CHEMICAL_FILTER = ITEMS.registerItem(
        "basic_chemical_filter",
        props -> new EnderChemicalFilterItem(props, EnderChemicalFilterItem.Type.BASIC)
    );

    static {
        ITEMS.addAlias(EnderIO.rl("chemical_filter"), BASIC_CHEMICAL_FILTER.getId());
    }

    public static final DeferredHolder<MenuType<?>, MenuType<EnderChemicalFilterMenu>> CHEMICAL_FILTER_MENU = 
        MENUS.register("chemical_filter", EnderChemicalFilterItem.Type.BASIC::openMenu);

    public static class Capabilities {
        public static final ItemCapability<ChemicalFilter, Void> CHEMICAL_FILTER = ItemCapability.createVoid(EnderIO.rl("chemical_filter"), ChemicalFilter.class);

        public static final BlockCapability<IChemicalHandler, Direction> CHEMICAL = BlockCapability.createSided(
                ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "chemical_handler"),
                IChemicalHandler.class);
        public static final BlockCapability<IHeatHandler, Direction> HEAT = BlockCapability.createSided(
                ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "heat_handler"), IHeatHandler.class);

        public static class Item {
            public static final ItemCapability<IChemicalHandler, Void> CHEMICAL = ItemCapability.createVoid(
                    ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "chemical_handler"),
                    IChemicalHandler.class);
        }
    }

    public static final Component CHEMICAL_CONDUIT_CHANGE_FLUID1 = tooltip("chemical/change_fluid1");
    public static final Component CHEMICAL_CONDUIT_CHANGE_FLUID2 = tooltip("chemical/change_fluid2");
    public static final MutableComponent CHEMICAL_CONDUIT_CHANGE_FLUID3 = tooltip("chemical/change_fluid3");

    private static final TagKey<Item> OSMIUM = ItemTags
            .create(ResourceLocation.fromNamespaceAndPath("c", "ingots/osmium"));

    private static MutableComponent tooltip(String path) {
        return Component.translatable(Util.makeDescriptionId("tooltip", EnderIO.rl("conduit/" + path)));
    }

    public static final ResourceKey<Conduit<?, ?>> CHEMICAL = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("chemical"));
    public static final ResourceKey<Conduit<?, ?>> PRESSURIZED_CHEMICAL = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.rl("pressurized_chemical"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_CHEMICAL = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.rl("ender_chemical"));
    public static final ResourceKey<Conduit<?, ?>> HEAT = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("heat"));

    @Override
    public void initialize(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        MENUS.register(modEventBus);
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_DATA_TYPES.register(modEventBus);
        CONDUIT_CONNECTION_CONFIG_TYPES.register(modEventBus);
        CONDUIT_NETWORK_CONTEXT_TYPES.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addToCreativeTabs);

        if (ModList.get().isLoaded("laserio")) {
            MekanismLaserIOCompat.init(modEventBus);
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
        context.register(HEAT, new HeatConduit(EnderIO.rl("block/conduit/heat"), Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(HEAT))));
        context.register(CHEMICAL,
            new ChemicalConduit(EnderIO.rl("block/conduit/chemical"), Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(CHEMICAL)),
            750));
        context.register(PRESSURIZED_CHEMICAL, new ChemicalConduit(EnderIO.rl("block/conduit/pressurized_chemical"),
            Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(PRESSURIZED_CHEMICAL)), 2_000));
        context.register(ENDER_CHEMICAL, new ChemicalConduit(EnderIO.rl("block/conduit/ender_chemical"),
            Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(ENDER_CHEMICAL)), 64_000));
    }

    @Override
    public void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        conditions.accept(HEAT, CONDITION);
        conditions.accept(CHEMICAL, CONDITION);
        conditions.accept(PRESSURIZED_CHEMICAL, CONDITION);
        conditions.accept(ENDER_CHEMICAL, CONDITION);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
        var mekRecipeOutput = recipeOutput.withConditions(CONDITION);

        var chemicalConduit = lookupProvider.holderOrThrow(CHEMICAL);
        var pressurizedChemicalConduit = lookupProvider.holderOrThrow(PRESSURIZED_CHEMICAL);
        var enderChemicalConduit = lookupProvider.holderOrThrow(ENDER_CHEMICAL);
        var heatConduit = lookupProvider.holderOrThrow(HEAT);

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getConduitItem(chemicalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "basic_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.rl("mek_basic_pressurized_tube"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS,
                        ConduitApiImpl.INSTANCE.getConduitItem(pressurizedChemicalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "advanced_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.rl("mek_advanced_pressurized_tube"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getConduitItem(enderChemicalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "elite_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.rl("mek_elite_pressurized_tube"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS,
                        ConduitApiImpl.INSTANCE.getConduitItem(pressurizedChemicalConduit, 8))
                .pattern("CCC")
                .pattern("CUC")
                .pattern("CCC")
                .define('C', ConduitIngredient.of(chemicalConduit))
                .define('U',
                        BuiltInRegistries.ITEM.get(
                                ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_infused")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.rl("mek_basic_pressurized_tube_upgrade"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getConduitItem(enderChemicalConduit, 8))
                .pattern("CCC")
                .pattern("CUC")
                .pattern("CCC")
                .define('C', ConduitIngredient.of(pressurizedChemicalConduit))
                .define('U',
                        BuiltInRegistries.ITEM.get(
                                ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_reinforced")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.rl("mek_advanced_pressurized_tube_upgrade"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getConduitItem(heatConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I',
                        BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID,
                                "advanced_thermodynamic_conductor")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.rl("mek_advanced_thermodynamic_conductor"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BASIC_CHEMICAL_FILTER.get())
                .pattern(" P ")
                .pattern("POP")
                .pattern(" P ")
                .define('P', Items.PAPER)
                .define('O', OSMIUM)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(OSMIUM)))
                .save(mekRecipeOutput, EnderIO.rl("mek_chemical_filter"));
    }
}
