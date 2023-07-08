package com.enderio.machines.common.network;

import com.enderio.core.common.network.Packet;
import com.enderio.machines.common.menu.CrafterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Optional;

public class UpdateCrafterTemplatePacket implements Packet {
    public final List<ItemStack> recipeInputs;

    public UpdateCrafterTemplatePacket(List<ItemStack> recipeInputs) {
        this.recipeInputs = recipeInputs;
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getSender() != null && recipeInputs.size() <= 9;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if (context.getSender().containerMenu instanceof CrafterMenu crafterMenu) {
            for (int i = 0; i < recipeInputs.size(); i++) {
                crafterMenu.slots.get(CrafterMenu.INPUTS_INDEX + i).set(recipeInputs.get(i));
            }
        }
    }

    public static class Handler extends Packet.PacketHandler<UpdateCrafterTemplatePacket> {

        @Override
        public UpdateCrafterTemplatePacket fromNetwork(FriendlyByteBuf buf) {
            List<ItemStack> inputs = buf.readList(FriendlyByteBuf::readItem);
            return new UpdateCrafterTemplatePacket(inputs);
        }

        @Override
        public void toNetwork(UpdateCrafterTemplatePacket packet, FriendlyByteBuf buf) {
            buf.writeCollection(packet.recipeInputs, (b, s) -> b.writeItemStack(s, false));
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }
    }
}
