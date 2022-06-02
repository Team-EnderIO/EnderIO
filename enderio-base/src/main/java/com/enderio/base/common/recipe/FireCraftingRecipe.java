package com.enderio.base.common.recipe;

import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.api.recipe.IEnderRecipe;
import com.enderio.base.common.init.EIORecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

public class FireCraftingRecipe implements IEnderRecipe<FireCraftingRecipe, Container> {
    private final ResourceLocation id;
    private final ResourceLocation lootTable;
    private final List<ResourceLocation> baseBlocks;
    private final List<ResourceLocation> dimensions;

    public FireCraftingRecipe(ResourceLocation id, ResourceLocation lootTable, List<ResourceLocation> baseBlocks, List<ResourceLocation> dimensions) {
        this.id = id;
        this.lootTable = lootTable;
        this.baseBlocks = baseBlocks;
        this.dimensions = dimensions;
    }

    public ResourceLocation getLootTable() {
        return lootTable;
    }

    public boolean isBaseValid(Block block) {
        return baseBlocks.contains(block.getRegistryName());
    }

    public boolean isDimensionValid(ResourceKey<Level> dimension) {
        return dimensions.contains(dimension.location());
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<List<ItemStack>> getAllInputs() {
        return List.of();
    }

    @Override
    public List<ItemStack> getAllOutputs() {
        return List.of();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
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
    public DataGenSerializer<FireCraftingRecipe, Container> getSerializer() {
        return EIORecipes.Serializer.FIRE_CRAFTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return EIORecipes.Types.FIRE_CRAFTING;
    }

    public static class Serializer extends DataGenSerializer<FireCraftingRecipe, Container> {

        public Serializer() {

        }

        @Override
        public FireCraftingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ResourceLocation lootTable = new ResourceLocation(pSerializedRecipe.get("lootTable").getAsString());

            List<ResourceLocation> baseBlocks = new ArrayList<>();
            JsonArray baseBlocksJson = pSerializedRecipe.getAsJsonArray("base_blocks");
            for (JsonElement baseBlock : baseBlocksJson) {
                baseBlocks.add(new ResourceLocation(baseBlock.getAsString()));
            }

            List<ResourceLocation> dimensions = new ArrayList<>();
            JsonArray dimensionsJson = pSerializedRecipe.getAsJsonArray("dimensions");
            for (JsonElement dimension : dimensionsJson) {
                dimensions.add(new ResourceLocation(dimension.getAsString()));
            }

            return new FireCraftingRecipe(pRecipeId, lootTable, baseBlocks, dimensions);
        }

        @Override
        public void toJson(FireCraftingRecipe recipe, JsonObject json) {
            JsonArray baseBlocksJson = new JsonArray();
            for (ResourceLocation baseBlock : recipe.baseBlocks) {
                baseBlocksJson.add(baseBlock.toString());
            }

            JsonArray dimensionsJson = new JsonArray();
            for (ResourceLocation dimension : recipe.dimensions) {
                dimensionsJson.add(dimension.toString());
            }

            json.addProperty("lootTable", recipe.lootTable.toString());
            json.add("base_blocks", baseBlocksJson);
            json.add("dimensions", dimensionsJson);
        }

        @Override
        public FireCraftingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            ResourceLocation lootTable = buffer.readResourceLocation();
            List<ResourceLocation> baseBlocks = buffer.readList(FriendlyByteBuf::readResourceLocation);
            List<ResourceLocation> dimensions = buffer.readList(FriendlyByteBuf::readResourceLocation);
            return new FireCraftingRecipe(recipeId, lootTable, baseBlocks, dimensions);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FireCraftingRecipe recipe) {
            buffer.writeResourceLocation(recipe.lootTable);
            buffer.writeCollection(recipe.baseBlocks, FriendlyByteBuf::writeResourceLocation);
            buffer.writeCollection(recipe.dimensions, FriendlyByteBuf::writeResourceLocation);
        }

    }
}
