package com.enderio.base.common.network;

import com.enderio.EnderIO;
import com.enderio.api.travel.ITravelTarget;
import com.enderio.api.travel.TravelRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record AddTravelTargetPacket(@Nullable ITravelTarget target) implements CustomPacketPayload {

    public static ResourceLocation ID = EnderIO.loc("add_travel_target");

    public AddTravelTargetPacket(FriendlyByteBuf buf) {
        this(TravelRegistry.deserialize(buf.readNbt()).orElse(null));
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeNbt(target.save());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
