package com.enderio.core.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class JsonUtil {
    public static JsonObject serializeItemStackWithoutNBT(ItemStack itemStack) {
        com.google.gson.JsonObject jsonobject = new com.google.gson.JsonObject();
        jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString());
        if (itemStack.getCount() > 1) {
            jsonobject.addProperty("count", itemStack.getCount());
        }

        return jsonobject;
    }

    // Supports old recipe schemas.
    public static ItemStack deserializeItemStackWithOldFormat(JsonElement jsonElement, boolean readNBT, boolean disallowsAirInRecipe) {
        ItemStack output;
        if (jsonElement.isJsonObject()) {
            output = CraftingHelper.getItemStack(jsonElement.getAsJsonObject(), readNBT, disallowsAirInRecipe);
        } else {
            ResourceLocation id = new ResourceLocation(jsonElement.getAsString());
            Item outputItem = ForgeRegistries.ITEMS.getValue(id);
            output = Objects.requireNonNull(outputItem, "Item must be specified!").getDefaultInstance();
        }

        return output;
    }
}
