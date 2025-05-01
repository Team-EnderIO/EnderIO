package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.soul.EmptySoulStorageIngredient;
import com.enderio.base.api.soul.FilledSoulStorageIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EIOIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, EnderIO.NAMESPACE);

    public static final DeferredHolder<IngredientType<?>, IngredientType<EmptySoulStorageIngredient>> EMPTY_SOUL_STORAGE = INGREDIENT_TYPES
            .register("empty_soul_storage", () -> new IngredientType<>(EmptySoulStorageIngredient.CODEC));

    public static final DeferredHolder<IngredientType<?>, IngredientType<FilledSoulStorageIngredient>> FILLED_SOUL_STORAGE = INGREDIENT_TYPES
            .register("filled_soul_storage", () -> new IngredientType<>(FilledSoulStorageIngredient.CODEC));

    public static void register(IEventBus bus) {
        INGREDIENT_TYPES.register(bus);
    }
}
