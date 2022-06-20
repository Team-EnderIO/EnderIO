package com.enderio.base.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.darksteel.upgrades.DarkSteelUpgradeRecipe;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.base.common.recipe.GrindingBallRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EIORecipes {

    //TODO: Create a Registrate method for RecipeSerializer

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,
        EnderIO.MODID);

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,
        EnderIO.MODID);

    public static final RegistryObject<RecipeType<GrindingBallRecipe>> GRINDINGBALL = registerType("grindingball");
    public static final RegistryObject<GrindingBallRecipe.Serializer> GRINDINGBALL_SERIALIZER = RECIPE_SERIALIZERS.register("grindingball",
        GrindingBallRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<FireCraftingRecipe>> FIRE_CRAFTING = registerType("fire_crafting");
    public static final RegistryObject<FireCraftingRecipe.Serializer> FIRE_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("fire_crafting",
        FireCraftingRecipe.Serializer::new);

    public static final RegistryObject<DarkSteelUpgradeRecipe.Serializer> DARK_STEEL_UPGRADE_SERIALIZER = RECIPE_SERIALIZERS.register("dark_steel_upgrade", DarkSteelUpgradeRecipe.Serializer::new);

    private static <I extends Recipe<?>> RegistryObject<RecipeType<I>> registerType(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(EnderIO.loc(name)));
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }
}
