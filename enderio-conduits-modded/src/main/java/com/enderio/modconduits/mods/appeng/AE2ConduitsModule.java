package com.enderio.modconduits.mods.appeng;

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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AE2ConduitsModule implements ConduitModule {

    public static final AE2ConduitsModule INSTANCE = new AE2ConduitsModule();

    private static final ModLoadedCondition CONDITION = new ModLoadedCondition("ae2");

    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_TYPE, ModdedConduits.REGISTRY_NAMESPACE);
    public static final DeferredRegister<ConduitDataType<?>> CONDUIT_DATA_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_DATA_TYPE, ModdedConduits.REGISTRY_NAMESPACE);

    public static final DeferredHolder<ConduitType<?>, ConduitType<MEConduit>> AE2_CONDUIT = CONDUIT_TYPES
        .register("me", () -> ConduitType.builder(MEConduit.CODEC)
            .exposeCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST).build());

    public static ResourceKey<Conduit<?>> NORMAL = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("me"));
    public static ResourceKey<Conduit<?>> DENSE = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("dense_me"));

    public static final Supplier<ConduitDataType<ConduitInWorldGridNodeHost>> DATA =
        CONDUIT_DATA_TYPES.register("me", () -> new ConduitDataType<>(ConduitInWorldGridNodeHost.CODEC, ConduitInWorldGridNodeHost.STREAM_CODEC,
            ConduitInWorldGridNodeHost::new));

    private static final Component LANG_ME_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.me"), "ME Conduit");
    private static final Component LANG_DENSE_ME_CONDUIT = addTranslation("item", EnderIOBase.loc("conduit.dense_me"), "Dense ME Conduit");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return ModdedConduits.REGILITE.lang().add(prefix, id, translation);
    }

    private static final TagKey<Item> COVERED_DENSE_CABLE = ItemTags.create(ResourceLocation.fromNamespaceAndPath("ae2", "covered_dense_cable"));
    private static final TagKey<Item> COVERED_CABLE = ItemTags.create(ResourceLocation.fromNamespaceAndPath("ae2", "covered_cable"));
    private static final TagKey<Item> GLASS_CABLE = ItemTags.create(ResourceLocation.fromNamespaceAndPath("ae2", "glass_cable"));

    static {
        // TODO: Ender IO 8 - remove backward compatibility.
        CONDUIT_TYPES.addAlias(EnderIOBase.loc("ae2"), AE2_CONDUIT.getId());
    }

    @Override
    public void register(IEventBus modEventBus) {
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_DATA_TYPES.register(modEventBus);
    }

    @Override
    public void bootstrapConduits(BootstrapContext<Conduit<?>> context) {
        context.register(NORMAL, new MEConduit(EnderIOBase.loc("block/conduit/me"), LANG_ME_CONDUIT, false));
        context.register(DENSE, new MEConduit(EnderIOBase.loc("block/conduit/dense_me"), LANG_DENSE_ME_CONDUIT, true));
    }

    @Override
    public void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions) {
        conditions.accept(NORMAL, CONDITION);
        conditions.accept(DENSE, CONDITION);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
        var ae2RecipeOutput = recipeOutput.withConditions(CONDITION);

        var normalConduit = lookupProvider.holderOrThrow(NORMAL);
        var denseConduit = lookupProvider.holderOrThrow(DENSE);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getStackForType(normalConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', COVERED_CABLE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(ae2RecipeOutput, EnderIOBase.loc("ae_covered_cable"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getStackForType(normalConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', GLASS_CABLE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(ae2RecipeOutput, EnderIOBase.loc("ae_glass_cable"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitApi.INSTANCE.getStackForType(denseConduit, 3))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', COVERED_DENSE_CABLE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(ae2RecipeOutput, EnderIOBase.loc("ae_covered_dense_cable"));
    }

    // TODO: Integration for this again.
    //       I'm personally not a fan of the integration system at current, so I am going to avoid implementing this right now.
    //       Explanation being - I think most of these integration-type things should be events that are conditionally hooked if a mod is loaded.
//    public Optional<BlockState> getFacadeOf(ItemStack stack) {
//        if (stack.getItem() instanceof IFacadeItem facadeItem) {
//            return Optional.of(facadeItem.getTextureBlockState(stack));
//        }
//        return Optional.empty();
//    }
}
