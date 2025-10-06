package com.enderio.enderio.init;

import com.enderio.core.common.recipes.RecipeTypeSerializerPair;
import com.enderio.core.common.recipes.WrappedShapedRecipe;
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

public class EIORecipes {

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            EnderIO.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, EnderIO.MOD_ID);

    public static final RecipeTypeSerializerPair<FireCraftingRecipe, FireCraftingRecipe.Serializer> FIRE_CRAFTING = register(
        "fire_crafting", FireCraftingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedEntityStorageRecipe>> SHAPED_ENTITY_STORAGE = RECIPE_SERIALIZERS
        .register("shaped_entity_storage",
            () -> new WrappedShapedRecipe.Serializer<>(ShapedEntityStorageRecipe::new));

    public static final RecipeTypeSerializerPair<EnchanterRecipe, EnchanterRecipe.Serializer> ENCHANTING = register(
            "enchanting", EnchanterRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<AlloySmeltingRecipe, AlloySmeltingRecipe.Serializer> ALLOY_SMELTING = register(
            "alloy_smelting", AlloySmeltingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<SagMillingRecipe, SagMillingRecipe.Serializer> SAG_MILLING = register(
            "sag_milling", SagMillingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<SlicingRecipe, SlicingRecipe.Serializer> SLICING = register("slicing",
            SlicingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<SoulBindingRecipe, SoulBindingRecipe.Serializer> SOUL_BINDING = register(
            "soul_binding", SoulBindingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<TankRecipe, TankRecipe.Serializer> TANK = register("tank",
            TankRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<PaintingRecipe, PaintingRecipe.Serializer> PAINTING = register(
            "painting", PaintingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<FermentingRecipe, FermentingRecipe.Serializer> VAT_FERMENTING = register(
            "vat_fermenting", FermentingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<WeatherChangeRecipe, WeatherChangeRecipe.Serializer> WEATHER_CHANGE = register(
            "weather_change", WeatherChangeRecipe.Serializer::new);

    private static <R extends Recipe<?>, S extends RecipeSerializer<? extends R>> RecipeTypeSerializerPair<R, S> register(
            String name, Supplier<S> serializerFactory) {
        var type = RECIPE_TYPES.<RecipeType<R>>register(name, () -> RecipeType.simple(EnderIO.rl(name)));
        var serializer = RECIPE_SERIALIZERS.register(name, serializerFactory);
        return new RecipeTypeSerializerPair<>(type, serializer);
    }

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
