package com.enderio.conduits.client.model.conduit.facades;

import com.enderio.base.common.item.tool.YetaWrenchItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.EnderIOConduits;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class YetaChunkRebuildHandler {
    @SubscribeEvent
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.MAINHAND &&
            event.getSlot() != EquipmentSlot.OFFHAND &&
            !(event.getEntity() instanceof LocalPlayer)) {
            return;
        }

        if (event.getTo().is(EIOTags.Items.WRENCH) ||
            event.getFrom().is(EIOTags.Items.WRENCH)) {
            FacadeHelper.rebuildChunkMeshes();
        }
    }
}
