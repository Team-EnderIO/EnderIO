package com.enderio.base.data.model.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

public class RotatingItemModel {

    // No longer using registrate as once we move EnderCore away, we're not going to have it.
    public static void create(Item item, ItemModelProvider prov) {
        // json so the BEWLR is used + perspectives

        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);

        // @formatter:off
        prov
            .getBuilder(registryName.getPath())
            .parent((new ModelFile.UncheckedModelFile("builtin/entity")))
            .transforms()
            .transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 2, 0).scale(0.5F, 0.5F, 0.5F).end()
            .transform(ItemDisplayContext.HEAD).rotation(0, 180, 0).translation(0, 13, 7).scale(1F, 1F, 1F).end()
            .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0, 0, 0).translation(0, 3, 1).scale(0.55F, 0.55F, 0.55F).end()
            .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, -90F, 25F).translation(1.13F, 3.2F, 1.13F).scale(0.68F, 0.68F, 0.68F).end()
            .transform(ItemDisplayContext.FIXED).rotation(0, 180, 0).translation(0, 0, 0).scale(1F, 1F, 1F).end()
            .end();
        // @formatter:on

        // json with the actual model
        prov
            .getBuilder(registryName.getPath() + "_helper")
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", new ResourceLocation(registryName.getNamespace(), "item/" + registryName.getPath()));
    }
}
