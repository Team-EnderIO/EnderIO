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

public enum PoweredSpawnerMode implements StringRepresentable {
    SPAWN(0, "spawn"), CAPTURE(1, "capture");

    public static final Codec<PoweredSpawnerMode> CODEC = StringRepresentable.fromEnum(PoweredSpawnerMode::values);
    public static final IntFunction<PoweredSpawnerMode> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, PoweredSpawnerMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
            v -> v.id);

    private final int id;
    private final String name;

    PoweredSpawnerMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static PoweredSpawnerMode parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }
}
