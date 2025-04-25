package com.enderio.machines.common.blocks.powered_spawner;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum MobSpawnMode implements StringRepresentable {
    /**
     * Create a new instance of the entity.
     */
    // TODO: Ender IO 8 - rename to "new"
    NEW(0, "entity_type"),

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

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static MobSpawnMode parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }
}
