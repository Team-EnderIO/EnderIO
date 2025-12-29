package com.enderio.core.data.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ModelHelper {

    public static TextureAtlasSprite getMissingTexture() {
        return Minecraft.getInstance()
            .getAtlasManager()
            .getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS)
            .getSprite(MissingTextureAtlasSprite.getLocation());
    }
}
