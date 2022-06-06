package com.enderio.base.common.block.glass;

import com.enderio.base.EnderIO;
import com.enderio.base.client.gui.IIcon;
import com.enderio.base.common.util.Vector2i;
import net.minecraft.resources.ResourceLocation;

public enum GlassLighting implements IIcon {
    NONE,
    BLOCKING,
    EMITTING;

    public static final ResourceLocation TEXTURE = EnderIO.loc("textures/item/overlay/fused_quartz_light_overlay.png");

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public Vector2i getIconSize() {
        return new Vector2i(32,32);
    }

    @Override
    public Vector2i getRenderSize() {
        return new Vector2i(16,16);
    }

    @Override
    public Vector2i getTexturePosition() {
        return switch (this) {
            case NONE, EMITTING -> new Vector2i(0,0);
            case BLOCKING -> new Vector2i(32,0);
        };
    }

    @Override
    public boolean shouldRender() {
        return this != GlassLighting.NONE;
    }

    public String shortName() {
        return switch (this) {
            case NONE -> "";
            case BLOCKING -> "d";
            case EMITTING -> "e";
        };
    }

    public String englishName() {
        return switch (this) {
            case NONE -> "";
            case BLOCKING -> "Dark";
            case EMITTING -> "Enlightened";
        };
    }
}
