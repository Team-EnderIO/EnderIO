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
import java.util.function.UnaryOperator;

public enum RedstoneControl implements IIcon, StringRepresentable {

    ALWAYS_ACTIVE(0, bool -> true, ApiLang.REDSTONE_ALWAYS_ACTIVE),
    ACTIVE_WITH_SIGNAL(1, bool -> bool, ApiLang.REDSTONE_ACTIVE_WITH_SIGNAL),
    ACTIVE_WITHOUT_SIGNAL(2, bool -> !bool, ApiLang.REDSTONE_ACTIVE_WITHOUT_SIGNAL),
    NEVER_ACTIVE(3, bool -> false, ApiLang.REDSTONE_NEVER_ACTIVE);

    private static final ResourceLocation TEXTURE = new ResourceLocation("enderio", "textures/gui/icons/redstone_control.png");
    private static final Vector2i SIZE = new Vector2i(16, 16);

    public static final Codec<RedstoneControl> CODEC = StringRepresentable.fromEnum(RedstoneControl::values);
    public static final IntFunction<RedstoneControl> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, RedstoneControl> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final UnaryOperator<Boolean> isActive;

    private final Vector2i pos;
    private final Component tooltip;

    RedstoneControl(int id, UnaryOperator<Boolean> isActive, Component tooltip) {
        this.id = id;
        this.isActive = isActive;
        pos = new Vector2i(16*ordinal(), 0);
        this.tooltip = tooltip;
    }

    public boolean isActive(boolean hasRedstone) {
        return isActive.apply(hasRedstone);
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
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
        return new Vector2i(64, 16);
    }

    @Override
    public Component getTooltip() {
        return tooltip;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
