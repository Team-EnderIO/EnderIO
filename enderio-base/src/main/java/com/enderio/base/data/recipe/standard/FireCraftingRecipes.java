package com.enderio.base.data.recipe.standard;

import com.enderio.base.EnderIO;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.data.recipe.EnderRecipeProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class FireCraftingRecipes extends EnderRecipeProvider {
    public FireCraftingRecipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedFireRecipe(
            EnderIO.loc("fire_crafting/infinity"),
            EnderIO.loc("fire_crafting/infinity"),
            List.of(Blocks.BEDROCK),
            List.of(),
            List.of(Level.OVERWORLD.location())));
    }

    protected static class FinishedFireRecipe extends EnderFinishedRecipe {

        private final ResourceLocation lootTable;
        private final List<Block> bases;
        private final List<TagKey<Block>> baseTags;
        private final List<ResourceLocation> dimensions;

        public FinishedFireRecipe(ResourceLocation id, ResourceLocation lootTable, List<Block> bases, List<TagKey<Block>> baseTags,
            List<ResourceLocation> dimensions) {
            super(id);
            this.lootTable = lootTable;
            this.bases = bases;
            this.baseTags = baseTags;
            this.dimensions = dimensions;
        }

        @Override
        protected Set<String> getModDependencies() {
            // TODO: Do we care for this?
            return Set.of();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray basesJson = new JsonArray();
            for (Block baseBlock : bases) {
                JsonObject obj = new JsonObject();
                obj.addProperty("block", ForgeRegistries.BLOCKS.getKey(baseBlock).toString());
                basesJson.add(obj);
            }
            for (TagKey<Block> tag : baseTags) {
                JsonObject obj = new JsonObject();
                obj.addProperty("tag", tag.location().toString());
                basesJson.add(obj);
            }

            JsonArray dimensionsJson = new JsonArray();
            for (ResourceLocation dimension : dimensions) {
                dimensionsJson.add(dimension.toString());
            }

            json.addProperty("lootTable", lootTable.toString());
            json.add("base_blocks", basesJson);
            json.add("dimensions", dimensionsJson);

            super.serializeRecipeData(json);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return EIORecipes.Serializer.FIRE_CRAFTING.get();
        }
    }
}
