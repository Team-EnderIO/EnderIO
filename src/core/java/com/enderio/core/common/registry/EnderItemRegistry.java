package com.enderio.core.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class EnderItemRegistry extends DeferredRegister.Items {
    protected EnderItemRegistry(String namespace) {
        super(namespace);
    }

    /**
     * Adds a new item to the list of entries to be registered and returns a {@link DeferredItem} that will be populated with the created item automatically.
     *
     * @param name The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param func A factory for the new item. The factory should not cache the created item.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #register(String, Supplier)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <I extends Item> EnderDeferredItem<I> register(String name, Function<ResourceLocation, ? extends I> func) {
        return (EnderDeferredItem<I>) super.register(name, func);
    }

    /**
     * Adds a new item to the list of entries to be registered and returns a {@link DeferredItem} that will be populated with the created item automatically.
     *
     * @param name The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param sup  A factory for the new item. The factory should not cache the created item.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #register(String, Function)
     */
    @Override
    public <I extends Item> EnderDeferredItem<I> register(String name, Supplier<? extends I> sup) {
        return this.register(name, key -> sup.get());
    }

    /**
     * Adds a new {@link BlockItem} for the given {@link Block} to the list of entries to be registered and
     * returns a {@link DeferredItem} that will be populated with the created item automatically.
     *
     * @param name       The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param block      The supplier for the block to create a {@link BlockItem} for.
     * @param properties The properties for the created {@link BlockItem}.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerBlockItem(String, Supplier)
     * @see #registerBlockItem(Holder, Item.Properties)
     * @see #registerBlockItem(Holder)
     */
    public EnderDeferredItem<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block, Item.Properties properties) {
        return this.register(name, key -> new BlockItem(block.get(), properties));
    }

    /**
     * Adds a new {@link BlockItem} for the given {@link Block} to the list of entries to be registered and
     * returns a {@link DeferredItem} that will be populated with the created item automatically.
     * This method uses the default {@link Item.Properties}.
     *
     * @param name  The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param block The supplier for the block to create a {@link BlockItem} for.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerBlockItem(String, Supplier, Item.Properties)
     * @see #registerBlockItem(Holder, Item.Properties)
     * @see #registerBlockItem(Holder)
     */
    public EnderDeferredItem<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block) {
        return this.registerBlockItem(name, block, new Item.Properties());
    }

    /**
     * Adds a new {@link BlockItem} for the given {@link Block} to the list of entries to be registered and
     * returns a {@link DeferredItem} that will be populated with the created item automatically.
     * Where the name is determined by the name of the given block.
     *
     * @param block      The {@link DeferredHolder} of the {@link Block} for the {@link BlockItem}.
     * @param properties The properties for the created {@link BlockItem}.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerBlockItem(String, Supplier, Item.Properties)
     * @see #registerBlockItem(String, Supplier)
     * @see #registerBlockItem(Holder)
     */
    public EnderDeferredItem<BlockItem> registerBlockItem(Holder<Block> block, Item.Properties properties) {
        return this.registerBlockItem(block.unwrapKey().orElseThrow().location().getPath(), block::value, properties);
    }

    /**
     * Adds a new {@link BlockItem} for the given {@link Block} to the list of entries to be registered and
     * returns a {@link DeferredItem} that will be populated with the created item automatically.
     * Where the name is determined by the name of the given block and uses the default {@link Item.Properties}.
     *
     * @param block The {@link DeferredHolder} of the {@link Block} for the {@link BlockItem}.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerBlockItem(String, Supplier, Item.Properties)
     * @see #registerBlockItem(String, Supplier)
     * @see #registerBlockItem(Holder, Item.Properties)
     */
    public EnderDeferredItem<BlockItem> registerBlockItem(Holder<Block> block) {
        return this.registerBlockItem(block, new Item.Properties());
    }

    /**
     * Adds a new item to the list of entries to be registered and returns a {@link DeferredItem} that will be populated with the created item automatically.
     *
     * @param name  The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param func  A factory for the new item. The factory should not cache the created item.
     * @param props The properties for the created item.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerItem(String, Function)
     * @see #registerItem(String, Item.Properties)
     * @see #registerItem(String)
     */
    public <I extends Item> EnderDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Item.Properties props) {
        return this.register(name, () -> func.apply(props));
    }

    /**
     * Adds a new item to the list of entries to be registered and returns a {@link DeferredItem} that will be populated with the created item automatically.
     * This method uses the default {@link Item.Properties}.
     *
     * @param name The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param func A factory for the new item. The factory should not cache the created item.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerItem(String, Function, Item.Properties)
     * @see #registerItem(String, Item.Properties)
     * @see #registerItem(String)
     */
    public <I extends Item> EnderDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func) {
        return this.registerItem(name, func, new Item.Properties());
    }

    /**
     * Adds a new {@link Item} with the given {@link Item.Properties properties} to the list of entries to be registered and
     * returns a {@link DeferredItem} that will be populated with the created item automatically.
     *
     * @param name  The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param props A factory for the new item. The factory should not cache the created item.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerItem(String, Function, Item.Properties)
     * @see #registerItem(String, Function)
     * @see #registerItem(String)
     */
    public EnderDeferredItem<Item> registerItem(String name, Item.Properties props) {
        return this.registerItem(name, Item::new, props);
    }

    /**
     * Adds a new {@link Item} with the default {@link Item.Properties properties} to the list of entries to be registered and
     * returns a {@link DeferredItem} that will be populated with the created item automatically.
     *
     * @param name The new item's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @return A {@link DeferredItem} that will track updates from the registry for this item.
     * @see #registerItem(String, Function, Item.Properties)
     * @see #registerItem(String, Function)
     * @see #registerItem(String, Item.Properties)
     */
    public EnderDeferredItem<Item> registerItem(String name) {
        return this.registerItem(name, Item::new, new Item.Properties());
    }

    @Override
    protected <I extends Item> EnderDeferredItem<I> createHolder(ResourceKey<? extends Registry<Item>> registryKey, ResourceLocation key) {
        return EnderDeferredItem.createItem(ResourceKey.create(registryKey, key));
    }

    public static EnderItemRegistry createRegistry(String modid) {
        return new EnderItemRegistry(modid);
    }

}
