package com.enderio.decoration.common.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent.Context;

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
    
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeVarInt(Block.getId(this.state));
    }
    
    static void handle(final ServerToClientLightUpdate message, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> ServerToClientLightUpdateHandler.handlePacket(message, ctx));
        ctx.get().setPacketHandled(true);
    }

}
