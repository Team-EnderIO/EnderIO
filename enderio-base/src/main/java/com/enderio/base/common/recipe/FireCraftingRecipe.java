package com.enderio.base.common.recipe;

import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.api.recipe.IEnderRecipe;
import com.enderio.base.EnderIO;
import com.enderio.base.common.init.EIORecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.List;

public class FireCraftingRecipe implements IEnderRecipe<FireCraftingRecipe, Container> {
    private final ResourceLocation id;
    private final ResourceLocation lootTable;
    private final List<Block> bases;
    private final List<TagKey<Block>> baseTags;
    private final List<ResourceLocation> dimensions;

    public FireCraftingRecipe(ResourceLocation id, ResourceLocation lootTable, List<Block> bases, List<TagKey<Block>> baseTags, List<ResourceLocation> dimensions) {
        this.id = id;
        this.lootTable = lootTable;
        this.bases = bases;
        this.baseTags = baseTags;
        this.dimensions = dimensions;
    }

    public ResourceLocation getLootTable() {
        return lootTable;
    }

    // Get all base blocks
    public List<Block> getBases() {
        List<Block> blocks = new ArrayList<>(bases);
        for (TagKey<Block> blockTagKey : baseTags) {
            ITag<Block> tag = ForgeRegistries.BLOCKS.tags().getTag(blockTagKey);
            blocks.addAll(tag.stream().toList());
        }
        return blocks;
    }

    public boolean isBaseValid(Block block) {
        for (TagKey<Block> tag : baseTags) {
            if (block.defaultBlockState().is(tag))
                return true;
        }
        return bases.contains(block);
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

            List<Block> baseBlocks = new ArrayList<>();
            List<TagKey<Block>> baseTags = new ArrayList<>();
            JsonArray baseBlocksJson = pSerializedRecipe.getAsJsonArray("base_blocks");
            for (JsonElement baseBlock : baseBlocksJson) {
                String id = baseBlock.getAsString();
                if (id.startsWith("#")) {
                    baseTags.add(BlockTags.create(new ResourceLocation(id.substring(1))));
                } else {
                    baseBlocks.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id)));
                }
            }

            List<ResourceLocation> dimensions = new ArrayList<>();
            JsonArray dimensionsJson = pSerializedRecipe.getAsJsonArray("dimensions");
            for (JsonElement dimension : dimensionsJson) {
                dimensions.add(new ResourceLocation(dimension.getAsString()));
            }

            return new FireCraftingRecipe(pRecipeId, lootTable, baseBlocks, baseTags, dimensions);
        }

        @Override
        public void toJson(FireCraftingRecipe recipe, JsonObject json) {
            JsonArray basesJson = new JsonArray();
            for (Block baseBlock : recipe.bases) {
                basesJson.add(baseBlock.getRegistryName().toString());
            }
            for (TagKey<Block> tag : recipe.baseTags) {
                basesJson.add("#" + tag.location().toString());
            }

            JsonArray dimensionsJson = new JsonArray();
            for (ResourceLocation dimension : recipe.dimensions) {
                dimensionsJson.add(dimension.toString());
            }

            json.addProperty("lootTable", recipe.lootTable.toString());
            json.add("base_blocks", basesJson);
            json.add("dimensions", dimensionsJson);
        }

        @Override
        public FireCraftingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                ResourceLocation lootTable = buffer.readResourceLocation();
                List<Block> baseBlocks = buffer.readList(buf -> ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation()));
                List<ResourceLocation> dimensions = buffer.readList(FriendlyByteBuf::readResourceLocation);
                return new FireCraftingRecipe(recipeId, lootTable, baseBlocks, List.of(), dimensions);
            } catch (Exception e) {
                EnderIO.LOGGER.error("Error reading fire crafting recipe from packet.", e);
                throw e;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FireCraftingRecipe recipe) {
            try {
                buffer.writeResourceLocation(recipe.lootTable);
                buffer.writeCollection(recipe.getBases(), (buf, block) -> buf.writeResourceLocation(block.getRegistryName()));
                buffer.writeCollection(recipe.dimensions, FriendlyByteBuf::writeResourceLocation);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing fire crafting recipe to packet.", ex);
                throw ex;
            }
        }

    }
}
