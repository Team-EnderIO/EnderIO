package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record C2SSetConduitConnectionState(
    BlockPos pos,
    Direction direction,
    IConduitType<?> conduitType,
    DynamicConnectionState connectionState
) implements CustomPacketPayload {

    public static final Type<C2SSetConduitConnectionState> TYPE = new Type<>(EnderCore.loc("c2s_conduit_connection_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSetConduitConnectionState> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        C2SSetConduitConnectionState::pos,
        Direction.STREAM_CODEC,
        C2SSetConduitConnectionState::direction,
        ByteBufCodecs.registry(ConduitTypes.REGISTRY.key()),
        C2SSetConduitConnectionState::conduitType,
        DynamicConnectionState.STREAM_CODEC,
        C2SSetConduitConnectionState::connectionState,
        C2SSetConduitConnectionState::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
