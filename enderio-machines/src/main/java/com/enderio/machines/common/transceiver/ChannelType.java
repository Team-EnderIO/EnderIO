package com.enderio.machines.common.transceiver;

import com.enderio.base.api.EnderIO;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum ChannelType  implements StringRepresentable {
    ITEM(0, "item", "Item", EnderIO.loc("icon/conduit_icon/item")),
    FLUID(1, "fluid", "Fluid", EnderIO.loc("icon/conduit_icon/fluid")),
    ENERGY(2, "energy", "Energy", EnderIO.loc("icon/conduit_icon/energy"));

    public final int id;
    public final String name;
    public final String tooltip;
    public final ResourceLocation icon;

    ChannelType(int id, String name, String tooltip, ResourceLocation icon) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.icon = icon;
    }

    public static final Codec<ChannelType> CODEC = StringRepresentable.fromEnum(ChannelType::values);
    
    public static final IntFunction<ChannelType> BY_ID = ByIdMap.continuous(key -> key.id, values(),
        ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<ByteBuf, ChannelType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    @Override
    public String getSerializedName() {
        return name;
    }
}

