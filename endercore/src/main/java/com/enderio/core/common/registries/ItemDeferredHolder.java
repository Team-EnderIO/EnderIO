package com.enderio.core.common.registries;

import com.google.common.collect.Multimap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ItemDeferredHolder<T extends Item> extends DeferredItem<T> {
    @Nullable Multimap<ResourceKey<CreativeModeTab>, BiConsumer<T, CreativeModeTab.Output>> tabOutputs;

    public static <T extends Item> DeferredItem<T> createItem(ResourceLocation key) {
        return createItem(ResourceKey.create(Registries.ITEM, key));
    }

    public static <T extends Item> DeferredItem<T> createItem(ResourceKey<Item> key) {
        return new ItemDeferredHolder<T>(key);
    }

    protected ItemDeferredHolder(ResourceKey<Item> key) {
        super(key);
    }
}
