package com.enderio.armory.client;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelEndabledUpdatePacket;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class TravelToggleHandler {

    public static void checkShiftStatus(MovementInputUpdateEvent evt) {
        ItemStack equipped = evt.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
        if (!DarkSteelHelper.hasUpgrade(equipped, TravelUpgrade.NAME)) {
            return;
        }
        equipped.set(EIODataComponents.TRAVEL_ITEM, evt.getInput().shiftKeyDown);
        PacketDistributor.sendToServer(new TravelEndabledUpdatePacket(evt.getInput().shiftKeyDown));
    }

}
