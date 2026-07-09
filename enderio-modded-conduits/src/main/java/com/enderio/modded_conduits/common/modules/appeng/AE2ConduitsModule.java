package com.enderio.modded_conduits.common.modules.appeng;

import appeng.api.AECapabilities;
import appeng.api.ids.AEItemIds;
import appeng.api.util.AEColor;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.enderio.init.EIOItems;
import com.enderio.modded_conduits.common.modules.ConduitCommonModule;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AE2ConduitsModule implements ConduitCommonModule {

    public static final AE2ConduitsModule INSTANCE = new AE2ConduitsModule();

    private static final ModLoadedCondition CONDITION = new ModLoadedCondition("ae2");

    public static final DeferredRegister<ConduitType<?, ?>> CONDUIT_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_TYPE, EnderIO.MOD_ID);

    public static final DeferredRegister<ConnectionConfigType<?>> CONDUIT_CONNECTION_CONFIG_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE, EnderIO.MOD_ID);

    public static final DeferredRegister<NodeDataType<?>> CONDUIT_NODE_DATA_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_NODE_DATA_TYPE, EnderIO.MOD_ID);

    public static final DeferredHolder<ConduitType<?, ?>, ConduitType<MEConduit, MEConduitConnectionConfig>> AE2_CONDUIT = CONDUIT_TYPES.register(
            "me",
            () -> ConduitType.builder(MEConduit.CODEC, MEConduitConnectionConfig.TYPE)
                    .exposeCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST)
                    .build());

    public static Supplier<ConnectionConfigType<MEConduitConnectionConfig>> CONNECTION_CONFIG = CONDUIT_CONNECTION_CONFIG_TYPES
        .register("me", () -> MEConduitConnectionConfig.TYPE);

    public static final Supplier<NodeDataType<MEConduitNodeData>> ME_NODE_DATA = CONDUIT_NODE_DATA_TYPES.register("me",
            () -> MEConduitNodeData.TYPE);

    public static final ResourceKey<Conduit<?, ?>> NORMAL = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("me"));
    public static final ResourceKey<Conduit<?, ?>> DENSE = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("dense_me"));

    private static final TagKey<Item> COVERED_DENSE_CABLE = ItemTags
            .create(Identifier.fromNamespaceAndPath("ae2", "covered_dense_cable"));
    private static final TagKey<Item> COVERED_CABLE = ItemTags
            .create(Identifier.fromNamespaceAndPath("ae2", "covered_cable"));
    private static final TagKey<Item> GLASS_CABLE = ItemTags
            .create(Identifier.fromNamespaceAndPath("ae2", "glass_cable"));
    @Override
    public void initialize(IEventBus modEventBus) {
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_CONNECTION_CONFIG_TYPES.register(modEventBus);
        CONDUIT_NODE_DATA_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerFacadeCapability);
    }

    private void registerFacadeCapability(RegisterCapabilitiesEvent event) {
        Item facadeItem = BuiltInRegistries.ITEM.getValue(AEItemIds.FACADE);
        event.registerItem(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER, AE2ConduitFacadeProvider.PROVIDER, facadeItem);
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?, ?>> context) {
        context.register(NORMAL,
                new MEConduit(EnderIO.id("block/conduit/me"), Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(NORMAL)),
                    AEColor.TRANSPARENT, false));
        context.register(DENSE,
                new MEConduit(EnderIO.id("block/conduit/dense_me"), Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(DENSE)),
                    AEColor.TRANSPARENT, true));

        // TODO: Colored ME conduit.
    }

    @Override
    public void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        conditions.accept(NORMAL, CONDITION);
        conditions.accept(DENSE, CONDITION);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
        var ae2RecipeOutput = recipeOutput.withConditions(CONDITION);

        var items = lookupProvider.lookupOrThrow(Registries.ITEM);
        var normalConduit = lookupProvider.holderOrThrow(NORMAL);
        var denseConduit = lookupProvider.holderOrThrow(DENSE);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitStackTemplate(normalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', COVERED_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(ae2RecipeOutput, EnderIO.id("ae_covered_cable").toString());

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitStackTemplate(normalConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', GLASS_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(ae2RecipeOutput, EnderIO.id("ae_glass_cable").toString());

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitStackTemplate(denseConduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', COVERED_DENSE_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(ae2RecipeOutput, EnderIO.id("ae_covered_dense_cable").toString());
    }
}
