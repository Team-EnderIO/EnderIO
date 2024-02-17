package com.enderio.machines.common.network;

import com.enderio.EnderIO;
import com.enderio.machines.common.menu.CrafterMenu;
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
    public void handle(NetworkEvent.Context context) {
        if (context.getSender().containerMenu instanceof CrafterMenu crafterMenu) {
            for (int i = 0; i < recipeInputs.size(); i++) {
                crafterMenu.slots.get(CrafterMenu.INPUTS_INDEX + i).set(recipeInputs.get(i));
            }
        }
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
