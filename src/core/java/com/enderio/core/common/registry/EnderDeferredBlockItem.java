package com.enderio.core.common.registry;

import com.enderio.core.data.model.EnderItemModelProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.function.BiConsumer;

public class EnderDeferredBlockItem<T extends BlockItem, U extends Block> extends EnderDeferredItem<T>{
    private EnderDeferredBlock<U> block;
    protected EnderDeferredBlockItem(ResourceKey<Item> key) {
        super(key);
    }

    public static <U extends Block,T extends BlockItem> EnderDeferredBlockItem<T,U> create(EnderDeferredBlock<U> block, EnderDeferredItem<T> item) {
        EnderDeferredBlockItem<T,U> blockitem = new EnderDeferredBlockItem<>(item.getKey());
        blockitem.block = block;
        return blockitem;
    }

    public EnderDeferredBlock<U> finishBlockItem() {
        return block;
    }

    @Override
    public EnderDeferredBlockItem<T,U> setTab(ResourceKey<CreativeModeTab> tab) {
        this.tab = tab;
        return this;
    }

    @SafeVarargs
    public final EnderDeferredBlockItem<T,U> addBlockItemTags(TagKey<Item>... tags) {
        this.ItemTags = Set.of(tags);
        return this;
    }

    @Override
    public EnderDeferredBlockItem<T,U> setTranslation(String translation) {
        this.translation = translation;
        return this;
    }

    @Override
    public EnderDeferredBlockItem<T,U> setModelProvider(BiConsumer<EnderItemModelProvider, Item> modelProvider) {
        this.modelProvider = modelProvider;
        return this;
    }
}
