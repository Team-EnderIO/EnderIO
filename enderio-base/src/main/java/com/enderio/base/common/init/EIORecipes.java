package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.base.common.recipe.SoulPotCrafting;
import com.enderio.core.common.recipes.RecipeTypeSerializerPair;
import com.enderio.core.common.recipes.WrappedShapedRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIORecipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, EnderIO.NAMESPACE);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            EnderIO.NAMESPACE);

    public static final RecipeTypeSerializerPair<FireCraftingRecipe, FireCraftingRecipe.Serializer> FIRE_CRAFTING = register(
            "fire_crafting", FireCraftingRecipe.Serializer::new);

    public static final RecipeTypeSerializerPair<SoulPotCrafting, RecipeSerializer<SoulPotCrafting>> SOUL_POT = register("soul_pot", () -> new SimpleCraftingRecipeSerializer(SoulPotCrafting::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedEntityStorageRecipe>> SHAPED_ENTITY_STORAGE = RECIPE_SERIALIZERS
            .register("shaped_entity_storage",
                    () -> new WrappedShapedRecipe.Serializer<>(ShapedEntityStorageRecipe::new));

    private static <R extends Recipe<?>, S extends RecipeSerializer<? extends R>> RecipeTypeSerializerPair<R, S> register(
            String name, Supplier<S> serializerFactory) {
        var type = RECIPE_TYPES.<RecipeType<R>>register(name, () -> RecipeType.simple(EnderIO.loc(name)));
        var serializer = RECIPE_SERIALIZERS.register(name, serializerFactory);
        return new RecipeTypeSerializerPair<>(type, serializer);
    }

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }
}
