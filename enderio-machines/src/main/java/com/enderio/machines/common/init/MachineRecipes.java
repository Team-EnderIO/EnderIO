package com.enderio.machines.common.init;

import com.enderio.machines.EIOMachines;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.AlloySmeltingRecipeImpl;
import com.enderio.api.recipe.EnchanterRecipe;
import com.enderio.machines.common.recipe.EnchanterRecipeImpl;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MachineRecipes {

    public static class Serializer {
        private Serializer() {}

        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EIOMachines.MODID);

        public static final RegistryObject<EnchanterRecipeImpl.Serializer> ENCHANTING = RECIPE_SERIALIZER_REGISTRY.register("enchanting", EnchanterRecipeImpl.Serializer::new);
        public static final RegistryObject<AlloySmeltingRecipeImpl.Serializer> ALLOY_SMELTING = RECIPE_SERIALIZER_REGISTRY.register("alloy_smelting", AlloySmeltingRecipeImpl.Serializer::new);

        public static void classload() {
            RECIPE_SERIALIZER_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    public static class Types {
        private Types() {}

        public static final RecipeType<EnchanterRecipe> ENCHANTING = RecipeType.register(EIOMachines.MODID + ":enchanting");
        public static final RecipeType<AlloySmeltingRecipe> ALLOY_SMELTING = RecipeType.register(EIOMachines.MODID + ":alloy_smelting");

        public static void classload() {}
    }
}
