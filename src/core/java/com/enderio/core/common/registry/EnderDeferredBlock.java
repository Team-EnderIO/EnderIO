package com.enderio.core.common.registry;

import com.enderio.core.data.loot.EnderBlockLootProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EnderDeferredBlock<T extends Block> extends DeferredBlock<T> {
    private String translation = "";
    private Set<TagKey<Block>> blockTags = Set.of();
    @Nullable
    private BiConsumer<EnderBlockLootProvider, T>  lootTable;
    @Nullable
    private BiConsumer<BlockStateProvider, T> blockStateProvider;
    @Nullable
    private EnderItemRegistry registry;
    protected EnderDeferredBlock(ResourceKey<Block> key) {
        super(key);
    }

    public EnderDeferredBlock<T> setTranslation(String translation) {
        this.translation = translation;
        return this;
    }

    public String getTranslation() {
        return translation.isEmpty() ? StringUtils.capitalize(getId().getPath().replace('_', ' ')) : translation;
    }

    @SafeVarargs
    public final EnderDeferredBlock<T> addBlockTags(TagKey<Block>... tags) {
        blockTags = Set.of(tags);
        return this;
    }

    public Set<TagKey<Block>> getBlockTags() {
        return blockTags;
    }

    public EnderDeferredBlock<T> setLootTable(BiConsumer<EnderBlockLootProvider, T> lootTable) {
        this.lootTable = lootTable;
        return this;
    }

    @Nullable
    public BiConsumer<EnderBlockLootProvider, T> getLootTable() {
        return lootTable;
    }

    public EnderDeferredBlock<T> setBlockStateProvider(BiConsumer<BlockStateProvider, T> blockStateProvider) {
        this.blockStateProvider = blockStateProvider;
        return this;
    }

    @Nullable
    public BiConsumer<BlockStateProvider, T> getBlockStateProvider() {
        return blockStateProvider;
    }

    public void setRegistry(@Nullable EnderItemRegistry registry) {
        this.registry = registry;
    }

    public EnderDeferredBlockItem<? extends BlockItem, T> createBlockItem() {
        EnderDeferredItem<BlockItem> item = registry.registerBlockItem(this);
        return EnderDeferredBlockItem.create(this, item);
    }

    public EnderDeferredBlockItem<? extends BlockItem, T> createItem(Supplier<? extends BlockItem> sup) {
        EnderDeferredItem<BlockItem> item = registry.register(getId().getPath(), sup);
        return EnderDeferredBlockItem.create(this, item);
    }

    public static <T extends Block> EnderDeferredBlock<T> createBlock(ResourceLocation key) {
        return createBlock(ResourceKey.create(Registries.BLOCK, key));
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the specified {@link Block}.
     *
     * @param <T> The type of the target {@link Block}.
     * @param key The resource key of the target {@link Block}.
     */
    public static <T extends Block> EnderDeferredBlock<T> createBlock(ResourceKey<Block> key) {
        return new EnderDeferredBlock<>(key);
    }
}
