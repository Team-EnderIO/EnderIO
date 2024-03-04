package com.enderio.machines.common.network;

import com.enderio.EnderIO;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// Serverbound
public record UpdateCrafterTemplatePacket(List<ItemStack> recipeInputs) implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderIO.loc("update_crafter_template");

    public UpdateCrafterTemplatePacket(FriendlyByteBuf buf) {
        this(buf.readList(FriendlyByteBuf::readItem));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(recipeInputs, FriendlyByteBuf::writeItem);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
