package com.enderio.base.common.network;

import com.enderio.EnderIO;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Custom setblock packet to update light
 */
public record ServerToClientLightUpdate(BlockPos pos, BlockState state) implements CustomPacketPayload {

    public static ResourceLocation ID = EnderIO.loc("light_update");

    public ServerToClientLightUpdate(FriendlyByteBuf buf) {
        this(
            buf.readBlockPos(),
            Block.stateById(buf.readVarInt())
        );
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(Block.getId(state));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
