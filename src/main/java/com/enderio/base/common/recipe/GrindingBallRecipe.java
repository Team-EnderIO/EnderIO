package com.enderio.base.common.recipe;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.common.recipes.EnderRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
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

public class GrindingBallRecipe implements IGrindingBallData, EnderRecipe<Container> {
    private final Item item;
    private final float doublingChance;
    private final float bonusMultiplier;
    private final float powerUse;
    private final int durability;


    @Nullable
    private ResourceLocation grindingBallId;

    public GrindingBallRecipe(Item item, float doublingChance, float bonusMultiplier, float powerUse, int durability) {
        this.item = item;
        this.doublingChance = doublingChance;
        this.bonusMultiplier = bonusMultiplier;
        this.powerUse = powerUse;
        this.durability = durability;
    }

    public Item getItem() {
        return item;
    }
    
    @Override
    public boolean matches(Container container, Level level) {
        return container.getItem(0).is(item);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getGrindingBallId() {
        return Objects.requireNonNull(grindingBallId);
    }

    public void setGrindingBallId(ResourceLocation grindingBallId) {
        this.grindingBallId = grindingBallId;
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

        public static final Codec<GrindingBallRecipe> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(GrindingBallRecipe::getItem),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("grinding").forGetter(obj -> obj.doublingChance),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("chance").forGetter(GrindingBallRecipe::getBonusMultiplier),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("power").forGetter(GrindingBallRecipe::getPowerUse),
                ExtraCodecs.POSITIVE_INT.fieldOf("durability").forGetter(GrindingBallRecipe::getDurability))
            .apply(inst, GrindingBallRecipe::new));

        @Override
        public Codec<GrindingBallRecipe> codec() {
            return CODEC;
        }

        @Override
        @Nullable
        public GrindingBallRecipe fromNetwork(FriendlyByteBuf buffer) {
            ResourceLocation grindingBallId = buffer.readResourceLocation();
            Item grindingBall = BuiltInRegistries.ITEM.get(grindingBallId);

            float grinding = buffer.readFloat();
            float chance = buffer.readFloat();
            float power = buffer.readFloat();
            int durability = buffer.readInt();
            return new GrindingBallRecipe(grindingBall, grinding, chance, power, durability);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GrindingBallRecipe recipe) {
            buffer.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(recipe.item)));
            buffer.writeFloat(recipe.doublingChance);
            buffer.writeFloat(recipe.bonusMultiplier);
            buffer.writeFloat(recipe.powerUse);
            buffer.writeInt(recipe.durability);
        }
    }
}
