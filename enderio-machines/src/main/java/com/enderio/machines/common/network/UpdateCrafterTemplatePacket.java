package com.enderio.machines.common.network;

import com.enderio.EnderIOBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// Serverbound
public record UpdateCrafterTemplatePacket(List<ItemStack> recipeInputs) implements CustomPacketPayload {

    public static final Type<UpdateCrafterTemplatePacket> TYPE = new Type<>(EnderIOBase.loc("update_crafter_template"));

    public static StreamCodec<RegistryFriendlyByteBuf, UpdateCrafterTemplatePacket> STREAM_CODEC =
        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(UpdateCrafterTemplatePacket::new, UpdateCrafterTemplatePacket::recipeInputs);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
