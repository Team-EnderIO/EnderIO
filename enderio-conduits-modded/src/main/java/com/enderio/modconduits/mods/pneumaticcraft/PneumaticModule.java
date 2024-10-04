package com.enderio.modconduits.mods.pneumaticcraft;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOItems;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitNetworkContextType;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.common.conduit.ConduitApiImpl;
import com.enderio.modconduits.ConduitModule;
import com.enderio.modconduits.ModdedConduits;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.pressure.PressureTier;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
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
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PneumaticModule implements ConduitModule {

    public static final PneumaticModule INSTANCE = new PneumaticModule();

    private static final ModLoadedCondition CONDITION = new ModLoadedCondition("pneumaticcraft");

    public static class Types {

        private static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_TYPE,
            ModdedConduits.REGISTRY_NAMESPACE);

        public static final Supplier<ConduitType<PressureConduit>> PRESSURE = CONDUIT_TYPES.register("pressure", () -> ConduitType.builder(PressureConduit.CODEC)
            .exposeCapability(PNCCapabilities.AIR_HANDLER_MACHINE).build());
    }

    public static class NetworkContexts {

        public static final DeferredRegister<ConduitNetworkContextType<?>> CONDUIT_NETWORK_CONTEXT_TYPES =
            DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_NETWORK_CONTEXT_TYPE, ModdedConduits.REGISTRY_NAMESPACE);

        public static final Supplier<ConduitNetworkContextType<PressureConduitContext>> PRESSURE_NETWORK =
            CONDUIT_NETWORK_CONTEXT_TYPES.register("pressure", () -> new ConduitNetworkContextType<>(PressureConduitContext.CODEC,
                PressureConduitContext::new));
    }


    public static final ResourceKey<Conduit<?>> BASIC = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("basic_pressure"));
    public static final ResourceKey<Conduit<?>> REINFORCED = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("reinforced_pressure"));
    public static final ResourceKey<Conduit<?>> ADVANCED = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("advanced_pressure"));

    private static final Component LANG_BASIC_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.basic_pressure"), "Basic Pressure Conduit");
    private static final Component LANG_REINFORCED_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.reinforced_pressure"), "Reinforced Pressure Conduit");
    private static final Component LANG_ADVANCED_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.advanced_pressure"), "Advanced Pressure Conduit");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return ModdedConduits.REGILITE.addTranslation(prefix, id, translation);
    }
    @Override
    public void register(IEventBus modEventBus) {
        Types.CONDUIT_TYPES.register(modEventBus);
        NetworkContexts.CONDUIT_NETWORK_CONTEXT_TYPES.register(modEventBus);
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?>> context) {
        context.register(BASIC, new PressureConduit(EnderIOBase.loc("block/conduit/basic_pressure"), LANG_BASIC_CONDUIT, PressureTier.TIER_ONE));
        context.register(REINFORCED, new PressureConduit(EnderIOBase.loc("block/conduit/reinforced_pressure"), LANG_ADVANCED_CONDUIT, PressureTier.TIER_ONE_HALF));
        context.register(ADVANCED, new PressureConduit(EnderIOBase.loc("block/conduit/advanced_pressure"), LANG_ADVANCED_CONDUIT, PressureTier.TIER_TWO));
    }

    @Override
    public void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        conditions.accept(BASIC, CONDITION);
        conditions.accept(REINFORCED, CONDITION);
        conditions.accept(ADVANCED, CONDITION);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
        var pncRecipeOutput = recipeOutput.withConditions(CONDITION);

        var basicPressureConduit = lookupProvider.holderOrThrow(BASIC);
        var reinforcedPressureConduit = lookupProvider.holderOrThrow(REINFORCED);
        var advancedPressureConduit = lookupProvider.holderOrThrow(ADVANCED);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(basicPressureConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("pneumaticcraft", "pressure_tube")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(pncRecipeOutput, EnderIOBase.loc("pnc_pressure_tube"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(reinforcedPressureConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("pneumaticcraft", "reinforced_pressure_tube")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(pncRecipeOutput, EnderIOBase.loc("pnc_reinforced_pressure_tube"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApiImpl.INSTANCE.getStackForType(advancedPressureConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("pneumaticcraft", "advanced_pressure_tube")))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(pncRecipeOutput, EnderIOBase.loc("pnc_advanced_pressure_tube"));
    }
}
