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
        // TODO: Couln't get the texture loaded from the models directory so moved it to
        // blocks
        // TODO: This seems wrong but textures could only be found under either items or
        // blocks
//                .texture("0", registryName.getNamespace() + ":models/glider/" + registryName.getPath());
        prov.basicItem(item);
    }
}
