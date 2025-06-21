package com.enderio.modconduits.common.modules.refinedstorage;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitApi;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.node.NodeDataType;
import com.enderio.conduits.api.network.node.legacy.ConduitDataType;
import com.enderio.modconduits.common.ModdedConduits;
import com.enderio.modconduits.common.ModuleModIds;
import com.enderio.modconduits.common.modules.ConduitCommonModule;
import com.refinedmods.refinedstorage.common.content.Tags;
import com.refinedmods.refinedstorage.neoforge.RefinedStorageNeoForgeApiImpl;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
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

public class RefinedStorageCommonModule implements ConduitCommonModule {

    public static final ConduitCommonModule INSTANCE = new RefinedStorageCommonModule();
    private static final ModLoadedCondition CONDITION = new ModLoadedCondition(ModuleModIds.REFINED_STORAGE);

    public static class ConduitKeys {
        public static final ResourceKey<Conduit<?, ?>> RS = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT,
                EnderIO.loc("rs"));
    }

    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister
            .create(EnderIOConduitsRegistries.CONDUIT_TYPE, EnderIO.NAMESPACE);
    public static final DeferredRegister<ConnectionConfigType<?>> CONDUIT_CONNECTION_CONFIG_TYPES = DeferredRegister
            .create(EnderIOConduitsRegistries.CONDUIT_CONNECTION_CONFIG_TYPE, EnderIO.NAMESPACE);
    public static final DeferredRegister<ConduitDataType<?>> CONDUIT_DATA_TYPES = DeferredRegister
            .create(EnderIOConduitsRegistries.CONDUIT_DATA_TYPE, EnderIO.NAMESPACE);

    public static final DeferredRegister<NodeDataType<?>> CONDUIT_NODE_DATA_TYPES = DeferredRegister
            .create(EnderIOConduitsRegistries.CONDUIT_NODE_DATA_TYPE, EnderIO.NAMESPACE);

    public static final Supplier<ConduitType<RSConduit>> RS_CONDUIT = CONDUIT_TYPES.register("rs",
            () -> ConduitType.builder(RSConduit.CODEC)
                    .exposeCapability(
                            RefinedStorageNeoForgeApiImpl.INSTANCE.getNetworkNodeContainerProviderCapability())
                    .build());

    static {
        // Connection types
        CONDUIT_CONNECTION_CONFIG_TYPES.register("rs", () -> RSConduitConnectionConfig.TYPE);

        // Node datum
        CONDUIT_NODE_DATA_TYPES.register("rs", () -> RSConduitNodeData.TYPE);
    }

    // TODO: 1.22: Remove.
    public static final Supplier<ConduitDataType<RSNetworkHost>> LEGACY_DATA_TYPE = CONDUIT_DATA_TYPES.register("rs",
            () -> new ConduitDataType<>(RSNetworkHost.CODEC, RSNetworkHost.STREAM_CODEC, RSNetworkHost::new));

    private static final Component LANG_RS_CONDUIT = addTranslation("item", EnderIO.loc("rs"),
            "Refined Storage Conduit");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return ModdedConduits.REGILITE.addTranslation(prefix, id, translation);
    }

    @Override
    public void initialize(IEventBus modEventBus) {
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_CONNECTION_CONFIG_TYPES.register(modEventBus);
        CONDUIT_DATA_TYPES.register(modEventBus);
        CONDUIT_NODE_DATA_TYPES.register(modEventBus);
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?, ?>> context) {
        context.register(ConduitKeys.RS, new RSConduit(EnderIO.loc("block/conduit/rs"), LANG_RS_CONDUIT));
    }

    @Override
    public void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        conditions.accept(ConduitKeys.RS, CONDITION);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
        var rsRecipeOutput = recipeOutput.withConditions(CONDITION);

        var conduit = lookupProvider.holderOrThrow(ConduitKeys.RS);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getConduitItem(conduit, 3))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', Tags.CABLES)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(rsRecipeOutput, EnderIO.loc("rs_conduit"));
    }
}
