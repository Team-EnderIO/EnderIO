package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.base.api.soul.binding.ingredients.EmptySoulBindableIngredient;
import com.enderio.base.api.soul.binding.ingredients.FilledSoulStorageIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EIOIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, EnderIO.NAMESPACE);

    public static final DeferredHolder<IngredientType<?>, IngredientType<EmptySoulBindableIngredient>> EMPTY_SOUL_STORAGE = INGREDIENT_TYPES
            .register("empty_soul_storage", () -> new IngredientType<>(EmptySoulBindableIngredient.CODEC));

    public static final DeferredHolder<IngredientType<?>, IngredientType<FilledSoulStorageIngredient>> FILLED_SOUL_STORAGE = INGREDIENT_TYPES
            .register("filled_soul_storage", () -> new IngredientType<>(FilledSoulStorageIngredient.CODEC));

    public static final DeferredHolder<IngredientType<?>, IngredientType<AnySoulBindableIngredient>> ANY_SOUL_STORAGE = INGREDIENT_TYPES
            .register("any_soul_storage", () -> new IngredientType<>(AnySoulBindableIngredient.CODEC));

    public static void register(IEventBus bus) {
        INGREDIENT_TYPES.register(bus);
    }
}
