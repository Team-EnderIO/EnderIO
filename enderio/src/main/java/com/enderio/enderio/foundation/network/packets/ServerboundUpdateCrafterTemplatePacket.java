package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ServerboundUpdateCrafterTemplatePacket(List<ItemStack> recipeInputs) implements CustomPacketPayload {

    public static final Type<ServerboundUpdateCrafterTemplatePacket> TYPE = new Type<>(EnderIO.rl("update_crafter_template"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUpdateCrafterTemplatePacket> STREAM_CODEC =
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(ServerboundUpdateCrafterTemplatePacket::new, ServerboundUpdateCrafterTemplatePacket::recipeInputs);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
