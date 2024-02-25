package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record C2SSetConduitExtendedData(BlockPos pos, IConduitType<?> conduitType, CompoundTag extendedConduitData)
    implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderCore.loc("c2s_conduit_extended_data");

    public C2SSetConduitExtendedData(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), ConduitTypes.getRegistry().get(buf.readResourceLocation()), buf.readNbt());
    }

    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeBlockPos(pos);
        writeInto.writeResourceLocation(ConduitTypes.getRegistry().getKey(conduitType));
        writeInto.writeNbt(extendedConduitData);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
