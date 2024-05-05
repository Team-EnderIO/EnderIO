package com.enderio.base.common.recipe;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.common.recipes.EnderRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record GrindingBallRecipe(
    Item item,
    float doublingChance,
    float bonusMultiplier,
    float powerUse,
    int durability
) implements IGrindingBallData, EnderRecipe<Container> {

    @Override
    public boolean matches(Container container, Level level) {
        return container.getItem(0).is(item);
    }

    @Override
    public ItemStack assemble(Container container, HolderLookup.Provider lookupProvider) {
        return this.getResultItem(lookupProvider);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<GrindingBallRecipe> getSerializer() {
        return EIORecipes.GRINDING_BALL.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return EIORecipes.GRINDING_BALL.type().get();
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(item));
        return list;
    }
    
    @Override
    public float getOutputMultiplier() {
        return doublingChance;
    }
    
    @Override
    public float getBonusMultiplier() {
        return bonusMultiplier;
    }
    
    @Override
    public float getPowerUse() {
        return powerUse;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    public static class Serializer implements RecipeSerializer<GrindingBallRecipe> {

        public static final MapCodec<GrindingBallRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(GrindingBallRecipe::item),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("grinding").forGetter(GrindingBallRecipe::doublingChance),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("chance").forGetter(GrindingBallRecipe::bonusMultiplier),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("power").forGetter(GrindingBallRecipe::powerUse),
                ExtraCodecs.POSITIVE_INT.fieldOf("durability").forGetter(GrindingBallRecipe::durability))
            .apply(inst, GrindingBallRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GrindingBallRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM),
            GrindingBallRecipe::item,
            ByteBufCodecs.FLOAT,
            GrindingBallRecipe::doublingChance,
            ByteBufCodecs.FLOAT,
            GrindingBallRecipe::bonusMultiplier,
            ByteBufCodecs.FLOAT,
            GrindingBallRecipe::powerUse,
            ByteBufCodecs.INT,
            GrindingBallRecipe::durability,
            GrindingBallRecipe::new
        );

        @Override
        public MapCodec<GrindingBallRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GrindingBallRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
