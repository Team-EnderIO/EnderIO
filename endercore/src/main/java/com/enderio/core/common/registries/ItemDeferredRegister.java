package com.enderio.core.common.registries;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemDeferredRegister extends DeferredRegister.Items {

    public static ItemDeferredRegister create(String namespace) {
        return new ItemDeferredRegister(namespace);
    }

    protected ItemDeferredRegister(String namespace) {
        super(namespace);
    }

    public <T extends Item> Builder<T> builder(String name, Supplier<? extends T> factory) {
        return new Builder<>(name, factory);
    }

    public <T extends Item> Builder<T> builder(String name, Function<Item.Properties, ? extends T> factory) {
        return new Builder<>(name, () -> factory.apply(new Item.Properties()));
    }

    public <T extends Item> Builder<T> builder(String name, Function<Item.Properties, ? extends T> factory, Item.Properties properties) {
        return new Builder<>(name, () -> factory.apply(properties));
    }

    @Override
    protected <I extends Item> DeferredItem<I> createHolder(ResourceKey<? extends Registry<Item>> registryKey, ResourceLocation key) {
        return ItemDeferredHolder.createItem(key);
    }

    @Override
    public void register(IEventBus bus) {
        super.register(bus);
        bus.addListener(this::addCreativeTabs);
    }

    private void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        for (var entry : getEntries()) {
            if (entry instanceof ItemDeferredHolder<?> holder) {
                addCreativeTabsInternal(event, holder);
            }
        }
    }

    private <T extends Item> void addCreativeTabsInternal(BuildCreativeModeTabContentsEvent event, ItemDeferredHolder<T> holder) {
        if (holder.tabOutputs == null) {
            return;
        }

        var consumers = holder.tabOutputs.get(event.getTabKey());
        for (var consumer : consumers) {
            consumer.accept(holder.get(), event);
        }
    }

    public class Builder<T extends Item> {
        private final String name;
        private final Supplier<? extends T> factory;
        private final Multimap<ResourceKey<CreativeModeTab>, BiConsumer<T, CreativeModeTab.Output>> tabOutputs = ArrayListMultimap.create();

        public Builder(String name, Supplier<? extends T> factory) {
            this.name = name;
            this.factory = factory;
        }

        public Builder<T> tab(ResourceKey<CreativeModeTab> tab) {
            tabOutputs.put(tab, (item, output) -> output.accept(new ItemStack(item)));
            return this;
        }

        public Builder<T> tab(ResourceKey<CreativeModeTab> tab, CreativeModeTab.TabVisibility visibility) {
            tabOutputs.put(tab, (item, output) -> output.accept(new ItemStack(item), visibility));
            return this;
        }

        public Builder<T> tab(ResourceKey<CreativeModeTab> tab, BiConsumer<T, CreativeModeTab.Output> output) {
            tabOutputs.put(tab, output);
            return this;
        }

        public DeferredItem<T> build() {
            //noinspection unchecked
            var holder = (ItemDeferredHolder<T>)register(name, factory);
            holder.tabOutputs = tabOutputs;
            return holder;
        }
    }
}
