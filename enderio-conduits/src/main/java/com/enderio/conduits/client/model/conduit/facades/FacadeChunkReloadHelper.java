package com.enderio.conduits.client.model.conduit.facades;

import com.enderio.base.common.item.tool.YetaWrenchItem;
import com.enderio.conduits.EnderIOConduits;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class FacadeChunkReloadHelper {
    @SubscribeEvent
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.MAINHAND &&
            event.getSlot() != EquipmentSlot.OFFHAND) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        if (!(event.getEntity() instanceof Player player && player.getGameProfile().equals(minecraft.player.getGameProfile()))) {
            return;
        }

        if (event.getTo().getItem() instanceof YetaWrenchItem ||
            event.getFrom().getItem() instanceof YetaWrenchItem) {
            FacadeHelper.rebuildChunkMeshes();
        }
    }
}
