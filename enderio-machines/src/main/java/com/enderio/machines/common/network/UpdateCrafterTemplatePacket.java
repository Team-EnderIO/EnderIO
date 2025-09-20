package com.enderio.machines.common.network;

import com.enderio.base.api.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// Serverbound
public record UpdateCrafterTemplatePacket(List<ItemStack> recipeInputs) implements CustomPacketPayload {

    public static final Type<UpdateCrafterTemplatePacket> TYPE = new Type<>(EnderIO.loc("update_crafter_template"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCrafterTemplatePacket> STREAM_CODEC =
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(UpdateCrafterTemplatePacket::new, UpdateCrafterTemplatePacket::recipeInputs);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
