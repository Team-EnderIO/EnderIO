package com.enderio.machines.common.init;

import com.enderio.EnderIOBase;
import com.enderio.core.common.recipes.RecipeTypeSerializerPair;
import com.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import com.enderio.machines.common.blocks.enchanter.EnchanterRecipe;
import com.enderio.machines.common.blocks.fluid_tank.TankRecipe;
import com.enderio.machines.common.blocks.painting.PaintingRecipe;
import com.enderio.machines.common.blocks.sag_mill.SagMillingRecipe;
import com.enderio.machines.common.blocks.slicer.SlicingRecipe;
import com.enderio.machines.common.blocks.soul_binder.SoulBindingRecipe;
import com.enderio.machines.common.blocks.vat.FermentingRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MachineRecipes {

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            EnderIOBase.REGISTRY_NAMESPACE);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, EnderIOBase.REGISTRY_NAMESPACE);

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

    private static <I extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<I>> registerType(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(EnderIOBase.loc(name)));
    }

    private static <R extends Recipe<?>, S extends RecipeSerializer<? extends R>> RecipeTypeSerializerPair<R, S> register(
            String name, Supplier<S> serializerFactory) {
        var type = RECIPE_TYPES.<RecipeType<R>>register(name, () -> RecipeType.simple(EnderIOBase.loc(name)));
        var serializer = RECIPE_SERIALIZERS.register(name, serializerFactory);
        return new RecipeTypeSerializerPair<>(type, serializer);
    }

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
