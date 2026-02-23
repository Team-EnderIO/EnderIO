package com.enderio.enderio.content.fire_crafting;

import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record FireCraftingRecipe(List<Result> results, List<Block> bases, List<TagKey<Block>> baseTags, List<ResourceKey<Level>> dimensions,
                                 Optional<Block> blockAfterBurning, PlacementInfo placementInfo) implements Recipe<RecipeInput> {

    public static final MapCodec<FireCraftingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(Codec.list(Result.CODEC).fieldOf("results").forGetter(FireCraftingRecipe::results),
            BuiltInRegistries.BLOCK.byNameCodec()
                .listOf()
                .optionalFieldOf("base_blocks", List.of())
                .forGetter(FireCraftingRecipe::bases),
            TagKey.codec(Registries.BLOCK)
                .listOf()
                .optionalFieldOf("base_tags", List.of())
                .forGetter(FireCraftingRecipe::baseTags),
            ResourceKey.codec(Registries.DIMENSION)
                .listOf()
                .fieldOf("dimensions")
                .forGetter(FireCraftingRecipe::dimensions),
            BuiltInRegistries.BLOCK.byNameCodec()
                .optionalFieldOf("block_after_burning")
                .forGetter(FireCraftingRecipe::blockAfterBurning))
        .apply(inst, FireCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FireCraftingRecipe> STREAM_CODEC = StreamCodec
        .composite(Result.STREAM_CODEC.apply(ByteBufCodecs.list()), FireCraftingRecipe::results,
            ByteBufCodecs.registry(Registries.BLOCK).apply(ByteBufCodecs.list()), FireCraftingRecipe::bases,
            Identifier.STREAM_CODEC.map(loc -> TagKey.create(Registries.BLOCK, loc), TagKey::location)
                .apply(ByteBufCodecs.list()),
            FireCraftingRecipe::baseTags,
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()),
            FireCraftingRecipe::dimensions,
            ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.BLOCK)),
            FireCraftingRecipe::blockAfterBurning, FireCraftingRecipe::new);

    public static final RecipeSerializer<FireCraftingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public FireCraftingRecipe(List<Result> results, List<Block> bases, List<TagKey<Block>> baseTags,
        List<ResourceKey<Level>> dimensions, Optional<Block> blockAfterBurning) {
        this(results, bases, baseTags, dimensions, blockAfterBurning, PlacementInfo.NOT_PLACEABLE);
    }
    // Get all base blocks
    public List<Block> getAllBaseBlocks() {
        List<Block> blocks = new ArrayList<>(bases);
        for (TagKey<Block> blockTagKey : baseTags) {
            BuiltInRegistries.BLOCK.get(blockTagKey)
                    .map(HolderSet.ListBacked::stream)
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
        return dimensions.contains(dimension);
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<FireCraftingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return EIORecipes.FIRE_CRAFTING.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.FIRE.get();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    public record Result(ItemStack result, int minCount, int maxCount, float chance) {
        public static final Codec<Result> CODEC = RecordCodecBuilder
                .create(resultInstance -> resultInstance
                        .group(ItemStack.CODEC.fieldOf("result").forGetter(Result::result),
                                Codec.INT.fieldOf("min_count").forGetter(Result::minCount),
                                Codec.INT.fieldOf("max_count").forGetter(Result::maxCount),
                                Codec.FLOAT.fieldOf("chance").forGetter(Result::chance))
                        .apply(resultInstance, Result::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Result> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Result::result, ByteBufCodecs.INT, Result::minCount, ByteBufCodecs.INT,
                Result::maxCount, ByteBufCodecs.FLOAT, Result::chance, Result::new);
    }
}
