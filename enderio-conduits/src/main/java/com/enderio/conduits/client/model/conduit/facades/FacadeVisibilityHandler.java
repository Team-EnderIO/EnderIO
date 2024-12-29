package com.enderio.conduits.client.model.conduit.facades;

import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.EnderIOConduits;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class FacadeVisibilityHandler {
    @SubscribeEvent
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.MAINHAND && event.getSlot() != EquipmentSlot.OFFHAND
                && !(event.getEntity() instanceof LocalPlayer)) {
            return;
        }

        if (event.getTo().is(EIOTags.Items.HIDE_FACADES) || event.getFrom().is(EIOTags.Items.HIDE_FACADES)) {

            // Check both hands directly in case of an offhand swap.
            ItemStack mainHand = event.getEntity().getItemBySlot(EquipmentSlot.MAINHAND);
            ItemStack offHand = event.getEntity().getItemBySlot(EquipmentSlot.OFFHAND);
            FacadeHelper.setFacadesVisible(
                    !mainHand.is(EIOTags.Items.HIDE_FACADES) && !offHand.is(EIOTags.Items.HIDE_FACADES));

            FacadeHelper.rebuildChunkMeshes();
        }
    }
}
