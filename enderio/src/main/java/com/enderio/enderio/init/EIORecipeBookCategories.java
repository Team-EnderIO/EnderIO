package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIORecipeBookCategories {
    private static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, EnderIO.MOD_ID);

    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> ALLOY_SMELTING = RECIPE_BOOK_CATEGORIES.register("alloy_smelting", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> WEATHER = RECIPE_BOOK_CATEGORIES.register("weather", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> PAINTING = RECIPE_BOOK_CATEGORIES.register("painting", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> SAG_MILL = RECIPE_BOOK_CATEGORIES.register("sag_mill", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> SLICING = RECIPE_BOOK_CATEGORIES.register("slicing", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> SOUL_BINDING = RECIPE_BOOK_CATEGORIES.register("soul_binding", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> FIRE = RECIPE_BOOK_CATEGORIES.register("fire", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> TANK = RECIPE_BOOK_CATEGORIES.register("tank", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> ENCHANTING = RECIPE_BOOK_CATEGORIES.register("enchanting", RecipeBookCategory::new);
    public static DeferredHolder<RecipeBookCategory, RecipeBookCategory> FERMENTING = RECIPE_BOOK_CATEGORIES.register("fermenting", RecipeBookCategory::new);

    public static void register(IEventBus bus) {
        RECIPE_BOOK_CATEGORIES.register(bus);
    }

}
