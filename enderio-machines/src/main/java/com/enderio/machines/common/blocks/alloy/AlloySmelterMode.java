package com.enderio.machines.common.blocks.alloy;

import com.enderio.core.common.network.NetworkDataSlot;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

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

    public static final NetworkDataSlot.CodecType<AlloySmelterMode> DATA_SLOT_TYPE = new NetworkDataSlot.CodecType<>(
            AlloySmelterMode.CODEC, AlloySmelterMode.STREAM_CODEC.cast());

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
}
