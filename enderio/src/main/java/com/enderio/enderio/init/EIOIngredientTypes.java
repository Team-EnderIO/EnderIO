package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.EmptySoulBindableIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.FilledSoulStorageIngredient;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.crafting.IngredientType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EIOIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(ForgeRegistries.Keys.INGREDIENT_TYPES, EnderIO.MOD_ID);

    public static void register(IEventBus bus) {
        INGREDIENT_TYPES.register("empty_soul_storage", () -> EmptySoulBindableIngredient.TYPE);
        INGREDIENT_TYPES.register("filled_soul_storage", () -> FilledSoulStorageIngredient.TYPE);
        INGREDIENT_TYPES.register("any_soul_storage", () -> AnySoulBindableIngredient.TYPE);
        INGREDIENT_TYPES.register("conduit", () -> ConduitIngredient.TYPE);

        INGREDIENT_TYPES.register(bus);
    }
}
