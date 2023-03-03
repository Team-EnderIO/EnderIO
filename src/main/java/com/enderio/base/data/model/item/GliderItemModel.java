package com.enderio.base.data.model.item;

import com.enderio.EnderIO;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class GliderItemModel {

    public static void create(Item item, RegistrateItemModelProvider prov) {

        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        prov.getBuilder(registryName.getNamespace() + ":enderio_glider/" + registryName.getPath())
            .parent(prov.getExistingFile(EnderIO.loc("glider/glider3d")))
            .texture("0", registryName.getNamespace() + ":models/glider/" + registryName.getPath());
        prov.generated(() -> item);
    }
}
