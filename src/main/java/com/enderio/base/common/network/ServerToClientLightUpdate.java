package com.enderio.base.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Custom setblock packet to update light
 */
public class ServerToClientLightUpdate {
    public final BlockPos pos;
    public final BlockState state;
    
    public ServerToClientLightUpdate(BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
    }
    
    public ServerToClientLightUpdate(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.state = Block.stateById(buf.readVarInt());
    }
    
    public static void write(ServerToClientLightUpdate msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.pos);
        buffer.writeVarInt(Block.getId(msg.state));
    }
    
    static void handle(final ServerToClientLightUpdate message, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> ServerToClientLightUpdateHandler.handlePacket(message, ctx));
        ctx.setPacketHandled(true);
    }

}
