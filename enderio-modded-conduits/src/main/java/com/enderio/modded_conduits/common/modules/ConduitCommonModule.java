package com.enderio.modded_conduits.common.modules;

import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.neoforged.bus.api.IEventBus;

public interface ConduitCommonModule {
    void initialize(IEventBus modEventBus);

    void bootstrapConduits(BootstrapContext<Conduit<?, ?>> context);

    void buildRecipes(RecipeOutput recipeOutput);
}
