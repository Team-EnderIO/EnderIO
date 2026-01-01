package com.enderio.core.data.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;

public class ModelHelper {

    public static TextureAtlasSprite getMissingTexture() {
        return Minecraft.getInstance()
            .getAtlasManager()
            .getAtlasOrThrow(AtlasIds.BLOCKS)
            .getSprite(MissingTextureAtlasSprite.getLocation());
    }
}
