package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.RecipeTypeSerializerPair;
import com.enderio.machines.common.recipe.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MachineRecipes {

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, EnderIO.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnderIO.MODID);

    public static final RecipeTypeSerializerPair<EnchanterRecipe, EnchanterRecipe.Serializer> ENCHANTING = register("enchanting", EnchanterRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<AlloySmeltingRecipe, AlloySmeltingRecipe.Serializer> ALLOY_SMELTING = register("alloy_smelting", AlloySmeltingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<SagMillingRecipe, SagMillingRecipe.Serializer> SAGMILLING = register("sagmilling", SagMillingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<SlicingRecipe, SlicingRecipe.Serializer> SLICING = register("slicing", SlicingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<SoulBindingRecipe, SoulBindingRecipe.Serializer> SOUL_BINDING = register("soul_binding", SoulBindingRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<TankRecipe, TankRecipe.Serializer> TANK = register("tank", TankRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<PaintingRecipe, PaintingRecipe.Serializer> PAINTING = register("painting", PaintingRecipe.Serializer::new);

    private static <I extends Recipe<?>> RegistryObject<RecipeType<I>> registerType(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(EnderIO.loc(name)));
    }

    private static <R extends Recipe<?>, S extends RecipeSerializer<? extends R>> RecipeTypeSerializerPair<R, S> register(String name, Supplier<S> serializerFactory) {
        RegistryObject<RecipeType<R>> type = RECIPE_TYPES.register(name, () -> RecipeType.simple(EnderIO.loc(name)));
        RegistryObject<S> serializer = RECIPE_SERIALIZERS.register(name, serializerFactory);
        return new RecipeTypeSerializerPair<>(type, serializer);
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
