package com.enderio.enderio.conduits.common.init;

import com.enderio.enderio.api.EnderIO;
import com.enderio.enderio.conduits.common.recipe.ConduitIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ConduitIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, EnderIO.NAMESPACE);

    public static final DeferredHolder<IngredientType<?>, IngredientType<ConduitIngredient>> CONDUIT_INGREDIENT_TYPE = INGREDIENT_TYPES
            .register("conduit", () -> new IngredientType<>(ConduitIngredient.CODEC));

    public static void register(IEventBus bus) {
        INGREDIENT_TYPES.register(bus);
    }
}
