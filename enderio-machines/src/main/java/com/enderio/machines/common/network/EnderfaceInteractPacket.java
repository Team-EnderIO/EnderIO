package com.enderio.machines.common.network;

import com.enderio.base.api.EnderIO;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.BlockHitResult;

public class EnderfaceInteractPacket implements CustomPacketPayload {
    public static final Type<EnderfaceInteractPacket> TYPE = new Type<>(EnderIO.loc("enderface_interact"));
    public static final StreamCodec<FriendlyByteBuf, EnderfaceInteractPacket> STREAM_CODEC = StreamCodec
            .ofMember(EnderfaceInteractPacket::write, EnderfaceInteractPacket::new);

    private final BlockHitResult blockHit;

    public EnderfaceInteractPacket(BlockHitResult blockHit) {
        this.blockHit = blockHit;
    }

    private EnderfaceInteractPacket(FriendlyByteBuf buffer) {
        this.blockHit = buffer.readBlockHitResult();
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeBlockHitResult(blockHit);
    }

    public BlockHitResult getHitResult() {
        return blockHit;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
