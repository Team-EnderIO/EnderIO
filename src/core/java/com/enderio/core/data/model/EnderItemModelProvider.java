package com.enderio.core.data.model;

import com.enderio.core.common.registry.EnderDeferredItem;
import com.enderio.core.common.registry.EnderItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;
import java.util.function.BiConsumer;

public class EnderItemModelProvider extends ItemModelProvider {
    private final EnderItemRegistry registry;

    public EnderItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper, EnderItemRegistry itemRegistry) {
        super(output, modid, existingFileHelper);
        this.registry = itemRegistry;
    }

    @Override
    protected void registerModels() {
        for (DeferredHolder<Item, ? extends Item> item : registry.getEntries()) {
            BiConsumer<EnderItemModelProvider, Item> modelProvider = ((EnderDeferredItem<? extends Item>) item).getModelProvider();
            if (modelProvider != null) {
                modelProvider.accept(this, item.get());
            }
        }
    }

    public ItemModelBuilder basicBlock(Item item) {
        return basicBlock(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    public ItemModelBuilder basicBlock(ResourceLocation item) {
        return getBuilder(item.toString())
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", new ResourceLocation(item.getNamespace(), "block/" + item.getPath()));
    }

    public ItemModelBuilder basicItem(Item item, ResourceLocation texture) {
        return getBuilder(BuiltInRegistries.ITEM.getKey(item).toString())
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", new ResourceLocation(texture.getNamespace(), "item/" + texture.getPath()));
    }

    public ResourceLocation itemTexture(ItemLike item) {
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.asItem()));
    }


}

