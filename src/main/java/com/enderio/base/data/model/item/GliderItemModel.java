package com.enderio.base.data.model.item;

import com.enderio.EnderIO;
import com.enderio.core.data.model.EnderItemModelProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class GliderItemModel {

    public static void create(EnderItemModelProvider prov, Item item) {

        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
        prov.getBuilder(registryName.getNamespace() + ":enderio_glider/" + registryName.getPath())
            .parent(prov.getExistingFile(EnderIO.loc("glider/glider3d")))
            .texture("0", registryName.getNamespace() + ":models/glider/" + registryName.getPath());
        prov.basicItem(item);
    }
}
