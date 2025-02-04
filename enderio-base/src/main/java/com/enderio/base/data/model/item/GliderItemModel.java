package com.enderio.base.data.model.item;

import com.enderio.base.api.EnderIO;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;

public class GliderItemModel {

    public static void create(Item item, ItemModelProvider prov) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
        prov.getBuilder(registryName.getNamespace() + ":enderio_glider/" + registryName.getPath())
                .parent(prov.getExistingFile(EnderIO.loc("glider/glider3d")))
                .texture("0", registryName.getNamespace() + ":block/glider/" + registryName.getPath());
        prov.basicItem(item);
    }
}
