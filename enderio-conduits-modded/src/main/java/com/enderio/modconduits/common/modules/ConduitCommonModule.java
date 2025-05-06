package com.enderio.modconduits.common.modules;

import com.enderio.conduits.api.Conduit;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;

public interface ConduitCommonModule {
    void initialize(IEventBus modEventBus);

    void bootstrapConduits(BootstrapContext<Conduit<?, ?>> context);

    void buildConduitConditions(BiConsumer<ResourceKey<?>, ICondition> conditions);

    void buildRecipes(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput);
}
