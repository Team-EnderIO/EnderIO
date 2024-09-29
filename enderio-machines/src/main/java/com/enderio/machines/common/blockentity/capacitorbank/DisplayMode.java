package com.enderio.machines.common.blockentity.capacitorbank;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum DisplayMode implements StringRepresentable {
    NONE(0, "none"),
    BAR(1, "bar"),
    IO(2, "io");

    public static final Codec<DisplayMode> CODEC = StringRepresentable.fromEnum(DisplayMode::values);
    public static final IntFunction<DisplayMode> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, DisplayMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String name;

    DisplayMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
