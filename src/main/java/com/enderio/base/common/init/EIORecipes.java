package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.base.common.recipe.GrindingBallRecipe;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.core.common.recipes.RecipeTypeSerializerPair;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EIORecipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnderIO.MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, EnderIO.MODID);


    public static final RecipeTypeSerializerPair<GrindingBallRecipe, GrindingBallRecipe.Serializer> GRINDING_BALL = register("grinding_ball", GrindingBallRecipe.Serializer::new);

    public static final RecipeTypeSerializerPair<FireCraftingRecipe, FireCraftingRecipe.Serializer> FIRE_CRAFTING = register("fire_crafting", FireCraftingRecipe.Serializer::new);

    public static final RegistryObject<ShapedEntityStorageRecipe.Serializer> SHAPED_ENTITY_STORAGE =
        RECIPE_SERIALIZERS.register("shaped_entity_storage", ShapedEntityStorageRecipe.Serializer::new);

    private static <R extends Recipe<?>, S extends RecipeSerializer<? extends R>> RecipeTypeSerializerPair<R, S> register(String name, Supplier<S> serializerFactory) {
        RegistryObject<RecipeType<R>> type = RECIPE_TYPES.register(name, () -> RecipeType.simple(EnderIO.loc(name)));
        RegistryObject<S> serializer = RECIPE_SERIALIZERS.register(name, serializerFactory);
        return new RecipeTypeSerializerPair<>(type, serializer);
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }
}
