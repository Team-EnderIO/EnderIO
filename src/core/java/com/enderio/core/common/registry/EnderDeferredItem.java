package com.enderio.core.common.registry;

import com.enderio.core.data.model.EnderItemModelProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.BiConsumer;

public class EnderDeferredItem<T extends Item> extends DeferredItem<T> implements ITranslatable{
    protected String translation = StringUtils.capitalize(getId().getPath().replace('_', ' '));
    protected Set<TagKey<Item>> ItemTags = Set.of();
    protected ResourceKey<CreativeModeTab> tab;
    @Nullable
    protected BiConsumer<EnderItemModelProvider, Item> modelProvider = EnderItemModelProvider::basicItem;

    protected EnderDeferredItem(ResourceKey<Item> key) {
        super(key);
    }

    public EnderDeferredItem<T> setTranslation(String translation) {
        this.translation = translation;
        return this;
    }

    public String getTranslation() {
        return translation;
    }

    @SafeVarargs
    public final EnderDeferredItem<T> addItemTags(TagKey<Item>... tags) {
        ItemTags = Set.of(tags);
        return this;
    }

    public Set<TagKey<Item>> getItemTags() {
        return ItemTags;
    }
    public EnderDeferredItem<T> setTab(ResourceKey<CreativeModeTab> tab) {
        this.tab = tab;
        return this;
    }

    public ResourceKey<CreativeModeTab> getTab() {
        return tab;
    }

    public EnderDeferredItem<T> setModelProvider(BiConsumer<EnderItemModelProvider, Item> modelProvider) {
        this.modelProvider = modelProvider;
        return this;
    }

    public BiConsumer<EnderItemModelProvider, Item> getModelProvider() {
        return modelProvider;
    }

    public static <T extends Item> EnderDeferredItem<T> createItem(ResourceLocation key) {
        return createItem(ResourceKey.create(Registries.ITEM, key));
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the specified {@link Item}.
     *
     * @param <T> The type of the target {@link Item}.
     * @param key The resource key of the target {@link Item}.
     */
    public static <T extends Item> EnderDeferredItem<T> createItem(ResourceKey<Item> key) {
        return new EnderDeferredItem<>(key);
    }
}
