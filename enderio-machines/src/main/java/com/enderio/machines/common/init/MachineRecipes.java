package com.enderio.machines.common.init;

import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MachineRecipes {

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, EIOMachines.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EIOMachines.MODID);

    public static final RegistryObject<RecipeType<EnchanterRecipe>> ENCHANTING = registerType("enchanting");
    public static final RegistryObject<EnchanterRecipeSerializer> ENCHANTING_SERIALIZER = RECIPE_SERIALIZERS.register("enchanting", EnchanterRecipeSerializer::new);

        public static final RegistryObject<EnchanterRecipe.Serializer> ENCHANTING = RECIPE_SERIALIZER_REGISTRY.register("enchanting", EnchanterRecipe.Serializer::new);
        public static final RegistryObject<AlloySmeltingRecipe.Serializer> ALLOY_SMELTING = RECIPE_SERIALIZER_REGISTRY.register("alloy_smelting", AlloySmeltingRecipe.Serializer::new);
        public static final RegistryObject<SagMillingRecipe.Serializer> SAGMILLING = RECIPE_SERIALIZER_REGISTRY.register("sagmilling", SagMillingRecipe.Serializer::new);
        public static final RegistryObject<SlicingRecipe.Serializer> SLICING = RECIPE_SERIALIZER_REGISTRY.register("slicing", SlicingRecipe.Serializer::new);

    private static <I extends Recipe<?>> RegistryObject<RecipeType<I>> registerType(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(EnderIO.loc(name)));
    }

    public static class Types {
        private Types() {}

        public static final RecipeType<EnchanterRecipe> ENCHANTING = RecipeType.register(EIOMachines.MODID + ":enchanting");
        public static final RecipeType<IAlloySmeltingRecipe> ALLOY_SMELTING = RecipeType.register(EIOMachines.MODID + ":alloy_smelting");
        public static final RecipeType<SagMillingRecipe> SAGMILLING = RecipeType.register(EIOMachines.MODID + ":sagmilling");
        public static final RecipeType<SlicingRecipe> SLICING = RecipeType.register(EIOMachines.MODID + ":slicing");

        public static void classload() {}
    }
}
