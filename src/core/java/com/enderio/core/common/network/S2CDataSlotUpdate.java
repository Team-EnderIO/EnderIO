package com.enderio.core.common.network;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class S2CDataSlotUpdate implements Packet {

    private final BlockPos pos;

    // You shouldn't really send null, but its "technically" valid.
    @Nullable
    private final FriendlyByteBuf slotData;

    public S2CDataSlotUpdate(BlockPos pos, FriendlyByteBuf buf) {
        this.pos = pos;
        this.slotData = buf;
    }

    public S2CDataSlotUpdate(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        slotData = new FriendlyByteBuf(buf.copy());
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return slotData != null;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                enderBlockEntity.clientHandleBufferSync(slotData);
            }
        }
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
        writeInto.writeBytes(slotData);
    }

    public static class Handler extends PacketHandler<S2CDataSlotUpdate> {

        @Override
        public S2CDataSlotUpdate fromNetwork(FriendlyByteBuf buf) {
            return new S2CDataSlotUpdate(buf);
        }

        @Override
        public void toNetwork(S2CDataSlotUpdate packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
