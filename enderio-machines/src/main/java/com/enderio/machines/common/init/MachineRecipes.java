package com.enderio.machines.common.init;

import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.AlloySmeltingRecipeImpl;
import com.enderio.api.recipe.EnchanterRecipe;
import com.enderio.machines.common.recipe.EnchanterRecipeImpl;
import com.enderio.machines.common.recipe.serializer.AlloySmeltingRecipeSerializer;
import com.enderio.machines.common.recipe.serializer.EnchanterRecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;
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

    public static final RegistryObject<RecipeType<AlloySmeltingRecipe>> ALLOY_SMELTING = registerType("alloy_smelting");
    public static final RegistryObject<AlloySmeltingRecipeSerializer> ALLOY_SMELTING_SERIALIZER = RECIPE_SERIALIZERS.register("alloy_smelting", AlloySmeltingRecipeSerializer::new);

    private static <I extends Recipe<?>> RegistryObject<RecipeType<I>> registerType(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(EnderIO.loc(name)));
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
