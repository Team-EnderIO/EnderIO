package com.enderio.modconduits.mods.mekanism;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOItems;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitDataType;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.common.conduit.ConduitApiImpl;
import com.enderio.conduits.common.recipe.ConduitIngredient;
import com.enderio.modconduits.ConduitModule;
import com.enderio.modconduits.ModdedConduits;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MekanismModule implements ConduitModule {

    public static final MekanismModule INSTANCE = new MekanismModule();

    private static final ModLoadedCondition CONDITION = new ModLoadedCondition("mekanism");

    public static final DeferredRegister<ConduitDataType<?>> CONDUIT_DATA_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_DATA_TYPE,
        ModdedConduits.REGISTRY_NAMESPACE);

    public static class Types {

        private static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_TYPE,
            ModdedConduits.REGISTRY_NAMESPACE);

        public static final Supplier<ConduitType<ChemicalConduit>> CHEMICAL = CONDUIT_TYPES.register("chemical", () -> ConduitType.of(ChemicalConduit.CODEC));

        public static final Supplier<ConduitType<HeatConduit>> HEAT = CONDUIT_TYPES.register("heat", () -> ConduitType.of(HeatConduit::new));
    }

    public static class Capabilities {
        public static final BlockCapability<IGasHandler, Direction> GAS = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "gas_handler"), IGasHandler.class);
        public static final BlockCapability<ISlurryHandler, Direction> SLURRY = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "slurry_handler"), ISlurryHandler.class);
        public static final BlockCapability<IInfusionHandler, Direction> INFUSION = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "infusion_handler"), IInfusionHandler.class);
        public static final BlockCapability<IPigmentHandler, Direction> PIGMENT = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "pigment_handler"), IPigmentHandler.class);
        public static final BlockCapability<IHeatHandler, Direction> HEAT = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "heat_handler"), IHeatHandler.class);
    }

    public static final ResourceKey<Conduit<?>> CHEMICAL = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("chemical"));
    public static final ResourceKey<Conduit<?>> PRESSURIZED_CHEMICAL = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT,
        EnderIOBase.loc("pressurized_chemical"));
    public static final ResourceKey<Conduit<?>> ENDER_CHEMICAL = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("ender_chemical"));
    public static final ResourceKey<Conduit<?>> HEAT = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("heat"));

    public static final Supplier<ConduitDataType<ChemicalConduitData>> CHEMICAL_DATA_TYPE = CONDUIT_DATA_TYPES.register("chemical",
        () -> new ConduitDataType<>(ChemicalConduitData.CODEC, ChemicalConduitData.STREAM_CODEC, ChemicalConduitData::new));

    private static final Component LANG_HEAT_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.heat"), "Heat Conduit");
    private static final Component LANG_CHEMICAL_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.chemical"), "Chemical Conduit");
    private static final Component LANG_PRESSURIZED_CHEMICAL_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.pressurized_chemical"),
        "Pressurized Chemical Conduit");
    private static final Component LANG_ENDER_CHEMICAL_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.ender_chemical"), "Ender Chemical Conduit");

    public static final Component LANG_MULTI_CHEMICAL_TOOLTIP = addTranslation("item", EnderIOBase.loc("conduit.chemical.multi"),
        "Allows multiple chemical types to be transported on the same line");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return ModdedConduits.REGILITE.addTranslation(prefix, id, translation);
    }

    @Override
    public void register(IEventBus modEventBus) {
        Types.CONDUIT_TYPES.register(modEventBus);
        CONDUIT_DATA_TYPES.register(modEventBus);
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?>> context) {
        context.register(HEAT, new HeatConduit(EnderIOBase.loc("block/conduit/heat"), LANG_HEAT_CONDUIT));
        context.register(CHEMICAL, new ChemicalConduit(EnderIOBase.loc("block/conduit/chemical"), LANG_CHEMICAL_CONDUIT, 50, false));
        context.register(PRESSURIZED_CHEMICAL,
            new ChemicalConduit(EnderIOBase.loc("block/conduit/pressurized_chemical"), LANG_PRESSURIZED_CHEMICAL_CONDUIT, 100, false));
        context.register(ENDER_CHEMICAL, new ChemicalConduit(EnderIOBase.loc("block/conduit/ender_chemical"), LANG_ENDER_CHEMICAL_CONDUIT, 200, true));
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
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(chemicalConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "basic_pressurized_tube")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(mekRecipeOutput, EnderIOBase.loc("mek_basic_pressurized_tube"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(pressurizedChemicalConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "advanced_pressurized_tube")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(mekRecipeOutput, EnderIOBase.loc("mek_advanced_pressurized_tube"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(enderChemicalConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "elite_pressurized_tube")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(mekRecipeOutput, EnderIOBase.loc("mek_elite_pressurized_tube"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(pressurizedChemicalConduit, 8))
            .pattern("CCC")
            .pattern("CUC")
            .pattern("CCC")
            .define('C', ConduitIngredient.of(chemicalConduit))
            .define('U', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_infused")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(mekRecipeOutput, EnderIOBase.loc("mek_basic_pressurized_tube_upgrade"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(enderChemicalConduit, 8))
            .pattern("CCC")
            .pattern("CUC")
            .pattern("CCC")
            .define('C', ConduitIngredient.of(pressurizedChemicalConduit))
            .define('U', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_reinforced")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(mekRecipeOutput, EnderIOBase.loc("mek_advanced_pressurized_tube_upgrade"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(heatConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "advanced_thermodynamic_conductor")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(mekRecipeOutput, EnderIOBase.loc("mek_advanced_thermodynamic_conductor"));
    }
}
