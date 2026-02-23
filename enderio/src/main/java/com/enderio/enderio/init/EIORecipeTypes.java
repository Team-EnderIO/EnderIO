package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.enchanter.EnchanterRecipe;
import com.enderio.enderio.content.fire_crafting.FireCraftingRecipe;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherChangeRecipe;
import com.enderio.enderio.content.machines.painting.PaintingRecipe;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import com.enderio.enderio.foundation.soul.ShapedEntityStorageRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIORecipeTypes {

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, EnderIO.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnderIO.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FireCraftingRecipe>> FIRE_CRAFTING = register("fire_crafting",
        () -> FireCraftingRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedEntityStorageRecipe>> SHAPED_ENTITY_STORAGE = RECIPE_SERIALIZERS.register(
        "shaped_entity_storage", () -> ShapedEntityStorageRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<EnchanterRecipe>> ENCHANTING = register("enchanting", () -> EnchanterRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloySmeltingRecipe>> ALLOY_SMELTING = register("alloy_smelting",
        () -> AlloySmeltingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SagMillingRecipe>> SAG_MILLING = register("sag_milling", () -> SagMillingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SlicingRecipe>> SLICING = register("slicing", () -> SlicingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SoulBindingRecipe>> SOUL_BINDING = register("soul_binding",
        () -> SoulBindingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<TankRecipe>> TANK = register("tank", () -> TankRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PaintingRecipe>> PAINTING = register("painting", () -> PaintingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FermentingRecipe>> VAT_FERMENTING = register("vat_fermenting",
        () -> FermentingRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<WeatherChangeRecipe>> WEATHER_CHANGE = register("weather_change",
        () -> WeatherChangeRecipe.SERIALIZER);

    private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<R>> register(String name, Supplier<RecipeSerializer<R>> serializerSupplier) {
        var type = RECIPE_TYPES.<RecipeType<R>>register(name, () -> RecipeType.simple(EnderIO.id(name)));
        RECIPE_SERIALIZERS.register(name, serializerSupplier);
        return type;
    }

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
