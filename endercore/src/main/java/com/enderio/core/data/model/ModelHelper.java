package com.enderio.core.data.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;

public class ModelHelper {

    public static BlockModelBuilder getExistingAsBuilder(BlockModelProvider prov, ResourceLocation model) {
        return new BlockModelBuilder(null, prov.existingFileHelper).parent(prov.getExistingFile(model));
    }

    public static TextureAtlasSprite getMissingTexture() {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(MissingTextureAtlasSprite.getLocation());
    }
}
