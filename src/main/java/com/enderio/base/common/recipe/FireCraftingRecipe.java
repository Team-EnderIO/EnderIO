package com.enderio.base.common.recipe;

import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.common.recipes.EnderRecipe;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record FireCraftingRecipe(
    ResourceKey<LootTable> lootTable,
    int maxItemDrops,
    List<Block> bases,
    List<TagKey<Block>> baseTags,
    List<ResourceKey<Level>> dimensions)

    implements EnderRecipe<Container> {

    // Get all base blocks
    public List<Block> getAllBaseBlocks() {
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
        return dimensions.contains(dimension);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, HolderLookup.Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
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

        public static final MapCodec<FireCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(
                ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(FireCraftingRecipe::lootTable),
                Codec.INT.fieldOf("max_item_drops").forGetter(FireCraftingRecipe::maxItemDrops),
                BuiltInRegistries.BLOCK.byNameCodec().listOf().optionalFieldOf("base_blocks", List.of()).forGetter(FireCraftingRecipe::bases),
                TagKey.codec(Registries.BLOCK).listOf().optionalFieldOf("base_tags", List.of()).forGetter(FireCraftingRecipe::baseTags),
                ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("dimensions").forGetter(FireCraftingRecipe::dimensions))
            .apply(inst, FireCraftingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FireCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.LOOT_TABLE),
            FireCraftingRecipe::lootTable,
            ByteBufCodecs.INT,
            FireCraftingRecipe::maxItemDrops,
            ByteBufCodecs.registry(Registries.BLOCK).apply(ByteBufCodecs.list()),
            FireCraftingRecipe::bases,
            ResourceLocation.STREAM_CODEC
                .map(loc -> TagKey.create(Registries.BLOCK, loc), TagKey::location)
                .apply(ByteBufCodecs.list()),
            FireCraftingRecipe::baseTags,
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()),
            FireCraftingRecipe::dimensions,
            FireCraftingRecipe::new
        );

        @Override
        public MapCodec<FireCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FireCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
