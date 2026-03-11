package com.enderio.enderio.content.machines.powered_spawner;

import com.enderio.core.common.lang.EnumLangMap;
import com.enderio.enderio.EnderIO;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import java.util.function.IntFunction;

public enum PoweredSpawnerMode implements StringRepresentable {
    SPAWN(0, "spawn"), CAPTURE(1, "capture");

    public static final Codec<PoweredSpawnerMode> CODEC = StringRepresentable.fromEnum(PoweredSpawnerMode::values);
    public static final IntFunction<PoweredSpawnerMode> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, PoweredSpawnerMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
            v -> v.id);

    private static final EnumLangMap<PoweredSpawnerMode> LANG_MAP = new EnumLangMap<>(PoweredSpawnerMode.class, EnderIO.MOD_ID,
        "powered_spawner_mode");

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

    public Tag save() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public static PoweredSpawnerMode parse(Tag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    public MutableComponent getComponent() {
        return Objects.requireNonNull(LANG_MAP.get(this));
    }
}
