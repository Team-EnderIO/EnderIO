package com.enderio.api.misc;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum ColorControl implements Icon, StringRepresentable {
    GREEN(0, "green", 0xFF3B511A, 0xFF46611F),
    BROWN(1, "brown", 0xFF51301A, 0xFF61391F),
    BLUE(2, "blue", 0xFF253192, 0xFF2C3AAF),
    PURPLE(3, "purple", 0xFF7B2FBE, 0xFF9338E4),
    CYAN(4, "cyan", 0xFF287697, 0xFF308DB5),
    LIGHT_GRAY(5, "light_gray", 0xFF888888, 0xFFABABAB),
    GRAY(6, "gray", 0xFF434343, 0xFF505050),
    PINK(7, "pink", 0xFFAC6779, 0xFFD88198),
    LIME(8, "lime", 0xFF41CD34, 0xFF4EF63E),
    YELLOW(9, "yellow", 0xFFB1A521, 0xFFDECF2A),
    LIGHT_BLUE(10, "light_blue", 0xFF516DA8, 0xFF6689D3),
    MAGENTA(11, "magenta", 0xFF9C43A4, 0xFFC354CD),
    ORANGE(12, "orange", 0xFFBC6C36, 0xFFEB8844),
    WHITE(13, "white", 0xFFC0C0C0, 0xFFF0F0F0),
    BLACK(14, "black", 0xFF1E1B1B, 0xFF242020),
    RED(15, "red", 0xFFB3312C, 0xFFD63A34);

    private static final ResourceLocation TEXTURE = new ResourceLocation("enderio", "textures/gui/icons/color_control.png");
    private static final Vector2i SIZE = new Vector2i(16, 16);

    public static final Codec<ColorControl> CODEC = StringRepresentable.fromEnum(ColorControl::values);
    public static final IntFunction<ColorControl> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ColorControl> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String name;
    private final int color;
    private final int colorActive;

    private final Vector2i pos;

    ColorControl(int id, String name, int color, int colorActive) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.colorActive = colorActive;

        pos = new Vector2i(16 * ordinal(), 0);
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

    @Override
    public Vector2i getTextureSize() {
        return new Vector2i(256, 16);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
