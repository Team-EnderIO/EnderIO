package com.enderio.modded_conduits.common.modules.cc_tweaked;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.modded_conduits.common.ModuleModIds;
import com.enderio.modded_conduits.common.modules.ConduitCommonModule;
import dan200.computercraft.api.network.wired.WiredElementCapability;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CCConduitCommonModule implements ConduitCommonModule {

    public static final CCConduitCommonModule INSTANCE = new CCConduitCommonModule();
    private static final ModLoadedCondition CONDITION = new ModLoadedCondition(ModuleModIds.CC_TWEAKED);

    public static class ConduitKeys {
        public static final ResourceKey<Conduit<?, ?>> CC = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
                EnderIO.id("cc_tweaked"));
    }

    public static final DeferredRegister<ConduitType<?, ?>> CONDUIT_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_TYPE, EnderIO.MOD_ID);
    public static final DeferredRegister<ConnectionConfigType<?>> CONDUIT_CONNECTION_CONFIG_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE, EnderIO.MOD_ID);
    public static final DeferredRegister<NodeDataType<?>> CONDUIT_NODE_DATA_TYPES = DeferredRegister
            .create(EnderIORegistries.CONDUIT_NODE_DATA_TYPE, EnderIO.MOD_ID);

    public static final Supplier<ConduitType<CCConduit, CCConduitConnectionConfig>> CC_CONDUIT_TYPE = CONDUIT_TYPES.register("cc_tweaked",
            () -> ConduitType.builder(CCConduit.CODEC, CCConduitConnectionConfig.TYPE)
                    .exposeCapability(WiredElementCapability.get())
                    .build());

    static {
        CONDUIT_CONNECTION_CONFIG_TYPES.register("cc_tweaked", () -> CCConduitConnectionConfig.TYPE);
        CONDUIT_NODE_DATA_TYPES.register("cc_tweaked", () -> CCConduitNodeData.TYPE);
    }

    @Override
    public void initialize(IEventBus modEventBus) {
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_CONNECTION_CONFIG_TYPES.register(modEventBus);
        CONDUIT_NODE_DATA_TYPES.register(modEventBus);
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?, ?>> context) {
        context.register(ConduitKeys.CC, new CCConduit(EnderIO.id("block/conduit/cc_tweaked"),
                Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(ConduitKeys.CC))));
    }

    @Override
    public void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        conditions.accept(ConduitKeys.CC, CONDITION);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
        // Recipes intentionally deferred.
    }
}
