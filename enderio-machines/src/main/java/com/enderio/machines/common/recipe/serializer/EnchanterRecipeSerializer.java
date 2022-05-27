package com.enderio.machines.common.recipe.serializer;

import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.api.recipe.EnchanterRecipe;
import com.enderio.machines.common.recipe.EnchanterRecipeImpl;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public class EnchanterRecipeSerializer extends DataGenSerializer<EnchanterRecipe, Container> {

    @Override
    public EnchanterRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        Ingredient ingredient = Ingredient.fromJson(pSerializedRecipe.get("input").getAsJsonObject());
        Optional<Enchantment> enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(pSerializedRecipe.get("enchantment").getAsString()));
        if (enchantment.isEmpty()) {
            throw new ResourceLocationException("The enchantment in " + pRecipeId.toString() + " does not exist");
        }
        int amount = pSerializedRecipe.get("amount_per_level").getAsInt();
        int level = pSerializedRecipe.get("level").getAsInt();
        return new EnchanterRecipeImpl(pRecipeId, ingredient, enchantment.get(), amount, level);
    }

    @Override
    public EnchanterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
        Enchantment enchantment = Registry.ENCHANTMENT.get(pBuffer.readResourceLocation());
        int amount = pBuffer.readInt();
        int level = pBuffer.readInt();
        return new EnchanterRecipeImpl(pRecipeId, ingredient, enchantment, amount, level);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, EnchanterRecipe pRecipe) {
        pRecipe.getInput().toNetwork(pBuffer);
        pBuffer.writeResourceLocation(pRecipe.getEnchantment().getRegistryName());
        pBuffer.writeInt(pRecipe.getInputAmountPerLevel());
        pBuffer.writeInt(pRecipe.getLevelModifier());
    }

    @Override
    public void toJson(EnchanterRecipe recipe, JsonObject json) {
        json.addProperty("enchantment", recipe.getEnchantment().getRegistryName().toString());
        json.add("input", recipe.getInput().toJson());
        json.addProperty("amount_per_level", recipe.getInputAmountPerLevel());
        json.addProperty("level", recipe.getLevelModifier());
    }
}
