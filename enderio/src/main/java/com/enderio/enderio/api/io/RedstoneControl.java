package com.enderio.enderio.api.io;

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
import java.util.function.UnaryOperator;

public enum RedstoneControl implements StringRepresentable {

    ALWAYS_ACTIVE(0, "always_active", bool -> true, false),
    ACTIVE_WITH_SIGNAL(1, "active_with_signal", bool -> bool, true),
    ACTIVE_WITHOUT_SIGNAL(2, "active_without_signal", bool -> !bool, true),
    NEVER_ACTIVE(3, "never_active", bool -> false, false);

    public static final Codec<RedstoneControl> CODEC = StringRepresentable.fromEnum(RedstoneControl::values);
    public static final IntFunction<RedstoneControl> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, RedstoneControl> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private static final EnumLangMap<RedstoneControl> LANG_MAP = new EnumLangMap<>(RedstoneControl.class, EnderIO.MOD_ID,
        "redstone_control");

    private final int id;
    private final String name;
    private final UnaryOperator<Boolean> isActive;
    private final boolean isRedstoneSensitive;

    RedstoneControl(int id, String name, UnaryOperator<Boolean> isActive, boolean isRedstoneSensitive) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.isRedstoneSensitive = isRedstoneSensitive;
    }

    public boolean isActive(boolean hasRedstone) {
        return isActive.apply(hasRedstone);
    }

    public boolean isRedstoneSensitive() {
        return isRedstoneSensitive;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static RedstoneControl parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    public MutableComponent getComponent() {
        return Objects.requireNonNull(LANG_MAP.get(this));
    }
}
