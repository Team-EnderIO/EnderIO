package com.enderio.base.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.base.common.recipe.GrindingballRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EIORecipes {

    //TODO: Create a Registrate method for RecipeSerializer

    public static class Serializer {
        private Serializer() {}

        private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,
            EnderIO.MODID);

        public static final RegistryObject<GrindingballRecipe.Serializer> GRINDINGBALL = RECIPE_SERIALIZER_REGISTRY.register("grindingball",
            GrindingballRecipe.Serializer::new);

        public static final RegistryObject<FireCraftingRecipe.Serializer> FIRE_CRAFTING = RECIPE_SERIALIZER_REGISTRY.register("fire_crafting",
            FireCraftingRecipe.Serializer::new);

        public static void classload() {
            RECIPE_SERIALIZER_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    public static class Types {
        private Types() {}

        public static final RecipeType<GrindingballRecipe> GRINDINGBALL = RecipeType.register(EnderIO.MODID + ":grindingball");

        public static final RecipeType<FireCraftingRecipe> FIRE_CRAFTING = RecipeType.register(EnderIO.MODID + ":fire_crafting");

        public static void classload() {}
    }
}
