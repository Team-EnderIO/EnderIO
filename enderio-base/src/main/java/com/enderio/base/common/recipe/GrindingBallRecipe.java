package com.enderio.base.common.recipe;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.EnderIO;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.recipes.EnderRecipe;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;

public class GrindingBallRecipe implements IGrindingBallData, EnderRecipe<Container> {
    private final ResourceLocation id;
    private final Item item;
    private final float doublingChance;
    private final float bonusMultiplier;
    private final float powerUse;
    private final int durability;

    public GrindingBallRecipe(ResourceLocation id, Item item, float doublingChance, float bonusMultiplier, float powerUse, int durability) {
        this.id = id;
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
    public ItemStack assemble(Container container) {
        return this.getResultItem();
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<GrindingBallRecipe> getSerializer() {
        return EIORecipes.Serializer.GRINDINGBALL.get();
    }

    @Override
    public RecipeType<?> getType() {
        return EIORecipes.Types.GRINDINGBALL;
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

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<GrindingBallRecipe> {

        @Override
        public GrindingBallRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            ResourceLocation grindingBallId = new ResourceLocation(serializedRecipe.get("item").getAsString());
            Item grindingBall = ForgeRegistries.ITEMS.getValue(grindingBallId);
            if (grindingBall == null) {
                throw new ResourceLocationException("Grinding ball item not found!");
            }

            float grinding = serializedRecipe.get("grinding").getAsFloat();
            float chance = serializedRecipe.get("chance").getAsFloat();
            float power = serializedRecipe.get("power").getAsFloat();
            int durability = serializedRecipe.get("durability").getAsInt();
            return new GrindingBallRecipe(recipeId, grindingBall, grinding, chance, power, durability);
        }

        @Override
        public GrindingBallRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                ResourceLocation grindingBallId = buffer.readResourceLocation();
                Item grindingBall = ForgeRegistries.ITEMS.getValue(grindingBallId);
                if (grindingBall == null) {
                    throw new ResourceLocationException("Grinding ball item not found!");
                }

                float grinding = buffer.readFloat();
                float chance = buffer.readFloat();
                float power = buffer.readFloat();
                int durability = buffer.readInt();
                return new GrindingBallRecipe(recipeId, grindingBall, grinding, chance, power, durability);
            } catch (Exception e) {
                EnderIO.LOGGER.error("Error reading grinding ball recipe from packet.", e);
                throw e;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GrindingBallRecipe recipe) {
            try {
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.item)));
                buffer.writeFloat(recipe.doublingChance);
                buffer.writeFloat(recipe.bonusMultiplier);
                buffer.writeFloat(recipe.powerUse);
                buffer.writeInt(recipe.durability);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing grinding ball recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
