package com.enderio.enderio.content.machines.powered_spawner;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum MobSpawnMode implements StringRepresentable {
    /**
     * Create a new instance of the entity.
     */
    NEW(0, "new"),

    /**
     * Create an exact copy of the original entity.
     */
    COPY(1, "copy");

    public static final Codec<MobSpawnMode> CODEC = StringRepresentable.fromEnum(MobSpawnMode::values);
    public static final IntFunction<MobSpawnMode> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, MobSpawnMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String name;

    MobSpawnMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
