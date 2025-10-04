package com.enderio.enderio.common.init;

import com.enderio.enderio.common.EnderIO;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ConduitIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, EnderIO.MOD_ID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<ConduitIngredient>> CONDUIT_INGREDIENT_TYPE = INGREDIENT_TYPES
            .register("conduit", () -> ConduitIngredient.TYPE);

    public static void register(IEventBus bus) {
        INGREDIENT_TYPES.register(bus);
    }
}
