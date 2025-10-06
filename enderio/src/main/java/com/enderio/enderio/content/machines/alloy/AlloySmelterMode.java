package com.enderio.enderio.content.machines.alloy;

import com.enderio.core.common.lang.EnumLangMap;
import com.enderio.enderio.EnderIO;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Alloy smelter mode.
 * Also provides icon behaviours for GUI.
 */
public enum AlloySmelterMode implements StringRepresentable {
    /**
     * Furnace mode, only performs smelting recipes.
     */
    FURNACE(0, "furnace", false, true),

    /**
     * All mode, performs smelting and alloying.
     */
    ALL(1, "all", true, true),

    /**
     * Alloy mode, only performs alloying.
     */
    ALLOYS(2, "alloys", true, false);

    public static final Codec<AlloySmelterMode> CODEC = StringRepresentable.fromEnum(AlloySmelterMode::values);
    public static final IntFunction<AlloySmelterMode> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, AlloySmelterMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private static final EnumLangMap<AlloySmelterMode> LANG_MAP = new EnumLangMap<>(AlloySmelterMode.class, EnderIO.MOD_ID,
        "alloy_smelter_mode");

    private final int id;
    private final String name;
    private final boolean canAlloy;
    private final boolean canSmelt;

    AlloySmelterMode(int id, String name, boolean canAlloy, boolean canSmelt) {
        this.id = id;
        this.name = name;
        this.canAlloy = canAlloy;
        this.canSmelt = canSmelt;
    }

    public boolean canAlloy() {
        return canAlloy;
    }

    public boolean canSmelt() {
        return canSmelt;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public MutableComponent getComponent() {
        return Objects.requireNonNull(LANG_MAP.get(this));
    }
}
