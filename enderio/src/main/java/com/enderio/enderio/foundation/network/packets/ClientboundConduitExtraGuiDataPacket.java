package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ClientboundConduitExtraGuiDataPacket(int containerId, @Nullable CompoundTag extraGuiData)
        implements CustomPacketPayload {

    public static final Type<ClientboundConduitExtraGuiDataPacket> TYPE = new Type<>(EnderIO.rl("conduit_extra_gui_data"));

    public static final StreamCodec<ByteBuf, ClientboundConduitExtraGuiDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundConduitExtraGuiDataPacket::containerId,
            ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG).map(opt -> opt.orElse(null), Optional::ofNullable),
            ClientboundConduitExtraGuiDataPacket::extraGuiData, ClientboundConduitExtraGuiDataPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
