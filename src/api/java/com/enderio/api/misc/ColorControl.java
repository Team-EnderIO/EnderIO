package com.enderio.api.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum ColorControl implements IIcon {
    GREEN(0xFF3B511A, 0xFF46611F),
    BROWN(0xFF51301A, 0xFF61391F),
    BLUE(0xFF253192, 0xFF2C3AAF),
    PURPLE(0xFF7B2FBE, 0xFF9338E4),
    CYAN(0xFF287697, 0xFF308DB5),
    LIGHT_GRAY(0xFF888888, 0xFFABABAB),
    GRAY(0xFF434343, 0xFF505050),
    PINK(0xFFAC6779, 0xFFD88198),
    LIME(0xFF41CD34, 0xFF4EF63E),
    YELLOW(0xFFB1A521, 0xFFDECF2A),
    LIGHT_BLUE(0xFF516DA8, 0xFF6689D3),
    MAGENTA(0xFF9C43A4, 0xFFC354CD),
    ORANGE(0xFFBC6C36, 0xFFEB8844),
    WHITE(0xFFC0C0C0, 0xFFF0F0F0),
    BLACK(0xFF1E1B1B, 0xFF242020),
    RED(0xFFB3312C, 0xFFD63A34);

    private static final ResourceLocation TEXTURE = new ResourceLocation("enderio", "textures/gui/icons/color_control.png");
    private static final Vector2i SIZE = new Vector2i(12, 12);
    private final Vector2i pos;
    private final int color;
    private final int colorActive;

    ColorControl(int color, int colorActive) {
        pos = new Vector2i(12 * ordinal(), 0);
        this.color = color;
        this.colorActive = colorActive;
    }

    public int getColor() {
        return color;
    }

    public int getColorActive() {
        return colorActive;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public Component getTooltip() {
        return Component.literal(name().toLowerCase(Locale.ROOT).replace('_', ' '));
    }

    @Override
    public Vector2i getIconSize() {
        return SIZE;
    }

    @Override
    public Vector2i getTexturePosition() {
        return pos;
    }
}
