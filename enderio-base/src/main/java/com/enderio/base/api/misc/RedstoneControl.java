package com.enderio.base.api.misc;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public enum RedstoneControl implements StringRepresentable {

    ALWAYS_ACTIVE(0, "always_active", bool -> true),
    ACTIVE_WITH_SIGNAL(1, "active_with_signal", bool -> bool),
    ACTIVE_WITHOUT_SIGNAL(2, "active_without_signal", bool -> !bool),
    NEVER_ACTIVE(3, "never_active", bool -> false);

    public static final Codec<RedstoneControl> CODEC = StringRepresentable.fromEnum(RedstoneControl::values);
    public static final IntFunction<RedstoneControl> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, RedstoneControl> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String name;
    private final UnaryOperator<Boolean> isActive;

    RedstoneControl(int id, String name, UnaryOperator<Boolean> isActive) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
    }

    public boolean isActive(boolean hasRedstone) {
        return isActive.apply(hasRedstone);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
