package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.soul.ingredient.AnySoulIngredient;
import com.enderio.enderio.api.soul.ingredient.MobCategorySoulIngredient;
import com.enderio.enderio.api.soul.ingredient.SimpleSoulIngredient;
import com.enderio.enderio.api.soul.ingredient.SoulIngredientType;
import com.enderio.enderio.foundation.soul.ingredient.EngineSoulIngredient;
import com.enderio.enderio.foundation.soul.ingredient.FarmSoulIngredient;
import com.enderio.enderio.foundation.soul.ingredient.SolarSoulIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOSoulIngredientTypes {
    private static final DeferredRegister<SoulIngredientType<?>> SOUL_INGREDIENT_TYPES = DeferredRegister.create(EnderIORegistries.SOUL_INGREDIENT_TYPES, EnderIO.MOD_ID);

    public static void register(IEventBus bus) {
        SOUL_INGREDIENT_TYPES.register("simple", () -> SimpleSoulIngredient.TYPE);
        SOUL_INGREDIENT_TYPES.register("any_entity", () -> AnySoulIngredient.TYPE);
        SOUL_INGREDIENT_TYPES.register("mob_category", () -> MobCategorySoulIngredient.TYPE);
        SOUL_INGREDIENT_TYPES.register("has_engine_soul_data", () -> EngineSoulIngredient.TYPE);
        SOUL_INGREDIENT_TYPES.register("has_farm_soul_data", () -> FarmSoulIngredient.TYPE);
        SOUL_INGREDIENT_TYPES.register("has_solar_soul_data", () -> SolarSoulIngredient.TYPE);
        SOUL_INGREDIENT_TYPES.register(bus);
    }
}
