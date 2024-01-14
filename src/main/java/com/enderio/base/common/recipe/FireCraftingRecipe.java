package com.enderio.base.common.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.common.recipes.EnderRecipe;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FireCraftingRecipe implements EnderRecipe<Container> {
    private final ResourceLocation lootTable;
    private final int maxItemDrops;
    private final List<Block> bases;
    private final List<TagKey<Block>> baseTags;
    private final List<ResourceLocation> dimensions;

    public FireCraftingRecipe(ResourceLocation lootTable, int maxItemDrops, List<Block> bases, List<TagKey<Block>> baseTags, List<ResourceLocation> dimensions) {
        this.lootTable = lootTable;
        this.maxItemDrops = maxItemDrops;
        this.bases = bases;
        this.baseTags = baseTags;
        this.dimensions = dimensions;
    }


    public ResourceLocation getLootTable() {
        return lootTable;
    }

    public int getMaxItemDrops() {
        return maxItemDrops;
    }

    // Get all base blocks
    public List<Block> getBases() {
        List<Block> blocks = new ArrayList<>(bases);
        for (TagKey<Block> blockTagKey : baseTags) {
            BuiltInRegistries.BLOCK.getTag(blockTagKey).map(HolderSet.ListBacked::stream)
                .orElse(Stream.empty())
                .map(Holder::value)
                .forEach(blocks::add);
        }
        return blocks;
    }

    public boolean isBaseValid(Block block) {
        for (TagKey<Block> tag : baseTags) {
            if (block.defaultBlockState().is(tag)) {
                return true;
            }
        }
        return bases.contains(block);
    }

    public boolean isDimensionValid(ResourceKey<Level> dimension) {
        return dimensions.contains(dimension.location());
    }

    public List<ResourceLocation> getValidDimensions() {
        return ImmutableList.copyOf(dimensions);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }


    @Override
    public RecipeSerializer<FireCraftingRecipe> getSerializer() {
        return EIORecipes.FIRE_CRAFTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return EIORecipes.FIRE_CRAFTING.type().get();
    }

    public static class Serializer implements RecipeSerializer<FireCraftingRecipe> {

        public static final Codec<FireCraftingRecipe> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(ResourceLocation.CODEC.fieldOf("lootTable").forGetter(FireCraftingRecipe::getLootTable),
                Codec.INT.fieldOf("maxItemDrops").forGetter(FireCraftingRecipe::getMaxItemDrops),
                BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("baseBlocks").forGetter(FireCraftingRecipe::getBases),
                TagKey.codec(Registries.BLOCK).listOf().fieldOf("baseTags").forGetter(obj -> obj.baseTags),
                ResourceLocation.CODEC.listOf().fieldOf("dimensions").forGetter(obj -> obj.dimensions))
            .apply(inst, FireCraftingRecipe::new));

        @Override
        public Codec<FireCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @Nullable FireCraftingRecipe fromNetwork(FriendlyByteBuf buffer) {
            ResourceLocation lootTable = buffer.readResourceLocation();
            int maxItemDrops = buffer.readInt();
            List<Block> baseBlocks = buffer.readList(buf -> BuiltInRegistries.BLOCK.get(buf.readResourceLocation()));
            List<TagKey<Block>> baseTags = buffer.readList(buf -> BlockTags.create(buf.readResourceLocation()));
            List<ResourceLocation> dimensions = buffer.readList(FriendlyByteBuf::readResourceLocation);
            return new FireCraftingRecipe(lootTable, maxItemDrops, baseBlocks, baseTags, dimensions);

        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FireCraftingRecipe recipe) {
            buffer.writeResourceLocation(recipe.lootTable);
            buffer.writeInt(recipe.maxItemDrops);
            buffer.writeCollection(recipe.bases, (buf, block) -> buf.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block))));
            buffer.writeCollection(recipe.baseTags, (buf, tag) -> buf.writeResourceLocation(tag.location()));
            buffer.writeCollection(recipe.dimensions, FriendlyByteBuf::writeResourceLocation);
        }

    }
}
