package com.enderio.base.common.recipe;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.api.recipe.DataGenSerializer;
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

public class GrindingBallRecipe implements IGrindingBallData, EnderRecipe<Container> {
    private final ResourceLocation id;
    private final Item item;
    private final float mainOutput;
    private final float bonusOutput;
    private final float powerUse;
    private final int durability;

    public GrindingBallRecipe(ResourceLocation id, Item item, float mainOutput, float bonusOutput, float powerUse, int durability) {
        this.id = id;
        this.item = item;
        this.mainOutput = mainOutput;
        this.bonusOutput = bonusOutput;
        this.powerUse = powerUse;
        this.durability = durability;
    }

    public Item getItem() {
        return item;
    }
    
    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return pContainer.getItem(0).is(item);
    }

    @Override
    public ItemStack assemble(Container pContainer) {
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
    public float getMainOutput() {
        return mainOutput;
    }
    
    @Override
    public float getBonusOutput() {
        return bonusOutput;
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
        public GrindingBallRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ResourceLocation grindingBallId = new ResourceLocation(pSerializedRecipe.get("item").getAsString());
            Item grindingBall = ForgeRegistries.ITEMS.getValue(grindingBallId);
            if (grindingBall == null) {
                throw new ResourceLocationException("Grinding ball item not found!");
            }

            float grinding = pSerializedRecipe.get("grinding").getAsFloat();
            float chance = pSerializedRecipe.get("chance").getAsFloat();
            float power = pSerializedRecipe.get("power").getAsFloat();
            int durability = pSerializedRecipe.get("durability").getAsInt();
            return new GrindingBallRecipe(pRecipeId, grindingBall, grinding, chance, power, durability);
        }

        @Override
        public GrindingBallRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            try {
                ResourceLocation grindingBallId = pBuffer.readResourceLocation();
                Item grindingBall = ForgeRegistries.ITEMS.getValue(grindingBallId);
                if (grindingBall == null) {
                    throw new ResourceLocationException("Grinding ball item not found!");
                }

                float grinding = pBuffer.readFloat();
                float chance = pBuffer.readFloat();
                float power = pBuffer.readFloat();
                int durability = pBuffer.readInt();
                return new GrindingBallRecipe(pRecipeId, grindingBall, grinding, chance, power, durability);
            } catch (Exception e) {
                EnderIO.LOGGER.error("Error reading grinding ball recipe from packet.", e);
                throw e;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, GrindingBallRecipe pRecipe) {
            try {
                pBuffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(pRecipe.item));
                pBuffer.writeFloat(pRecipe.mainOutput);
                pBuffer.writeFloat(pRecipe.bonusOutput);
                pBuffer.writeFloat(pRecipe.powerUse);
                pBuffer.writeInt(pRecipe.durability);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing grinding ball recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
