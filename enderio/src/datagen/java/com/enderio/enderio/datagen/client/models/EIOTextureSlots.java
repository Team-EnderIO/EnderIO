package com.enderio.enderio.datagen.client.models;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;

public class EIOTextureSlots {
    public static final TextureSlot PANEL = TextureSlot.create("panel");

    public static TextureMapping photovoltaicPanelAndSide(Block block) {
        var result = photovoltaicSide(block);
        result.put(EIOTextureSlots.PANEL, TextureMapping.getBlockTexture(block, "_top"));
        return result;
    }

    public static TextureMapping photovoltaicSide(Block block) {
        var result = new TextureMapping();
        result.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        return result;
    }
}
