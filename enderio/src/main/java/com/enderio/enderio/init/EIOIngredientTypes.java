package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.EmptySoulBindableIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.FilledSoulStorageIngredient;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

public class EIOIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, EnderIO.MOD_ID);

    public static void register(IEventBus bus) {
        INGREDIENT_TYPES.register("empty_soul_storage", () -> EmptySoulBindableIngredient.TYPE);
        INGREDIENT_TYPES.register("filled_soul_storage", () -> FilledSoulStorageIngredient.TYPE);
        INGREDIENT_TYPES.register("any_soul_storage", () -> AnySoulBindableIngredient.TYPE);
        INGREDIENT_TYPES.register("conduit", () -> ConduitIngredient.TYPE);

        INGREDIENT_TYPES.register(bus);
    }
}
