package com.enderio.base.common.network;

import com.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Custom setblock packet to update light
 */
public record ServerToClientLightUpdate(BlockPos pos, BlockState state) implements CustomPacketPayload {

    public static Type<ServerToClientLightUpdate> TYPE = new Type<>(EnderIO.loc("light_update"));

    public static StreamCodec<ByteBuf, ServerToClientLightUpdate> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ServerToClientLightUpdate::pos,
        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
        ServerToClientLightUpdate::state,
        ServerToClientLightUpdate::new
    );

    public ServerToClientLightUpdate(FriendlyByteBuf buf) {
        this(
            buf.readBlockPos(),
            Block.stateById(buf.readVarInt())
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
