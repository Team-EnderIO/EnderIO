package com.enderio.core.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

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
    }

    public class Builder<T extends Item> {
        private final String name;
        private final Supplier<? extends T> factory;

        public Builder(String name, Supplier<? extends T> factory) {
            this.name = name;
            this.factory = factory;
        }

        // TODO: Capabilties

        public DeferredItem<T> build() {
            //noinspection unchecked
            var holder = (ItemDeferredHolder<T>)register(name, factory);
            return holder;
        }
    }
}
