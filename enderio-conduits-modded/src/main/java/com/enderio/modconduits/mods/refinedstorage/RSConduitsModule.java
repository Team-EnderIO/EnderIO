package com.enderio.modconduits.mods.refinedstorage;

import appeng.api.AECapabilities;
import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOItems;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitApi;
import com.enderio.conduits.api.ConduitDataType;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.modconduits.ConduitModule;
import com.enderio.modconduits.ModdedConduits;
import com.refinedmods.refinedstorage.neoforge.RefinedStorageNeoForgeApiImpl;
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

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RSConduitsModule implements ConduitModule {

    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_TYPE, ModdedConduits.REGISTRY_NAMESPACE);
    public static final DeferredRegister<ConduitDataType<?>> CONDUIT_DATA_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_DATA_TYPE, ModdedConduits.REGISTRY_NAMESPACE);
    public static final ConduitModule INSTANCE = new RSConduitsModule();

    private static final ModLoadedCondition CONDITION = new ModLoadedCondition("refinedstorage");

    public static final Supplier<ConduitType<RSConduit>> RS2_CONDUIT = CONDUIT_TYPES
        .register("rs2", () -> ConduitType.builder(RSConduit.CODEC)
            .exposeCapability(RefinedStorageNeoForgeApiImpl.INSTANCE.getNetworkNodeContainerProviderCapability())
            .build());

    public static ResourceKey<Conduit<?>> RS2 = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("rs2"));

    public static final Supplier<ConduitDataType<RSNetworkHost>> DATA =
        CONDUIT_DATA_TYPES.register("rs2", () -> new ConduitDataType<>(RSNetworkHost.CODEC, RSNetworkHost.STREAM_CODEC,
            RSNetworkHost::new));

    private static final Component LANG_RS2_CONDUIT = addTranslation("item", EnderIOBase.loc("rs2"), "Refined Storage Conduit");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return ModdedConduits.REGILITE.addTranslation(prefix, id, translation);
    }

    @Override
    public void register(IEventBus modEventBus) {
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_DATA_TYPES.register(modEventBus);
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?>> context) {
        context.register(RS2, new RSConduit(EnderIOBase.loc("block/conduit/rs"), LANG_RS2_CONDUIT));
    }

    @Override
    public void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        conditions.accept(RS2, CONDITION);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
        var rs2RecipeOutput = recipeOutput.withConditions(CONDITION);

        var conduit = lookupProvider.holderOrThrow(RS2);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getStackForType(conduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOItems.CONDUIT_BINDER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(rs2RecipeOutput, EnderIOBase.loc("rs2_conduit"));
    }
}
