package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.AnyBindableSoulSourceIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.enderio.api.soul.storage.ingredients.EmptySoulHandlerIngredient;
import com.enderio.enderio.foundation.ingredients.AnySpawnEggIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EIOIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, EnderIO.MOD_ID);

    public static void register(IEventBus bus) {
        INGREDIENT_TYPES.register("empty_soul_handler", () -> EmptySoulHandlerIngredient.TYPE);
        INGREDIENT_TYPES.register("any_soul_bound", () -> AnySoulBindableIngredient.TYPE);
        INGREDIENT_TYPES.register("any_bindable_soul_source", () -> AnyBindableSoulSourceIngredient.TYPE);
        INGREDIENT_TYPES.register("conduit", () -> ConduitIngredient.TYPE);

        INGREDIENT_TYPES.register("any_spawn_egg", () -> AnySpawnEggIngredient.TYPE);

        INGREDIENT_TYPES.register(bus);
    }
}
