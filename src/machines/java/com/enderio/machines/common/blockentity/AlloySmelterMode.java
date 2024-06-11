package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.misc.Icon;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

/**
 * Alloy smelter mode.
 * Also provides icon behaviours for GUI.
 */
public enum AlloySmelterMode implements Icon, StringRepresentable {
    /**
     * Furnace mode, only performs smelting recipes.
     */
    FURNACE(0, "furnace", false, true, MachineLang.ALLOY_SMELTER_MODE_FURNACE),

    /**
     * All mode, performs smelting and alloying.
     */
    ALL(1, "all", true, true, MachineLang.ALLOY_SMELTER_MODE_ALL),

    /**
     * Alloy mode, only performs alloying.
     */
    ALLOYS(2, "alloys", true, false, MachineLang.ALLOY_SMELTER_MODE_ALLOY);

    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/icons/alloy_modes.png"); // TODO: Redo widgets
    private static final Vector2i SIZE = new Vector2i(16, 16);

    public static final Codec<AlloySmelterMode> CODEC = StringRepresentable.fromEnum(AlloySmelterMode::values);
    public static final IntFunction<AlloySmelterMode> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, AlloySmelterMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    public static final NetworkDataSlot.CodecType<AlloySmelterMode> DATA_SLOT_TYPE =
        new NetworkDataSlot.CodecType<>(AlloySmelterMode.CODEC, AlloySmelterMode.STREAM_CODEC.cast());

    private final int id;
    private final String name;
    private final boolean canAlloy;
    private final boolean canSmelt;
    private final Vector2i pos;
    private final Component tooltip;

    AlloySmelterMode(int id, String name, boolean canAlloy, boolean canSmelt, Component tooltip) {
        this.id = id;
        this.name = name;
        this.canAlloy = canAlloy;
        this.canSmelt = canSmelt;
        pos = new Vector2i( 48 + 16 * ordinal(), 0);
        this.tooltip = tooltip;
    }

    public boolean canAlloy() {
        return canAlloy;
    }

    public boolean canSmelt() {
        return canSmelt;
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
    public Component getTooltip() {
        return tooltip;
    }

    @Override
    public Vector2i getTextureSize() {
        return new Vector2i(48, 16);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}

