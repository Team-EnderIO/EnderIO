package com.enderio.core.data.model;

import com.enderio.core.common.registry.EnderDeferredItem;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;

public class EIOModel {

    public static BlockModelBuilder getExistingParent(BlockModelProvider prov, ResourceLocation model) {
        return new BlockModelBuilder(null, prov.existingFileHelper)
            .parent(prov.getExistingFile(model));
    }

    // region Item

    public static ItemModelBuilder fakeBlockModel(EnderItemModelProvider prov, Item item) {
        return prov.withExistingParent(BuiltInRegistries.ITEM.getKey(item).getPath(), prov.mcLoc("block/cube_all")).texture("all", prov.itemTexture(item));
    }

    public static ItemModelBuilder mimicItem(Item item, EnderDeferredItem<? extends Item> from, EnderItemModelProvider prov) {
        return prov.basicItem(item, prov.itemTexture(from));
    }

    // endregion

    // region Textures

    public static TextureAtlasSprite getMissingTexture() {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
    }

    // endregion
}
