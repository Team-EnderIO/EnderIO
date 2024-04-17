package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.connection.DynamicConnectionState;
import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record C2SSetConduitConnectionState(BlockPos pos, Direction direction, IConduitType<?> conduitType, DynamicConnectionState connectionState) implements
    CustomPacketPayload {

    public static final ResourceLocation ID = EnderCore.loc("c2s_conduit_connection_state");

    public C2SSetConduitConnectionState(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readEnum(Direction.class), ConduitTypes.getRegistry().get(buf.readResourceLocation()), DynamicConnectionState.fromNetwork(buf));
    }

    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
        writeInto.writeEnum(direction);
        writeInto.writeResourceLocation(ConduitTypes.getRegistry().getKey(conduitType));
        connectionState.toNetwork(writeInto);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
