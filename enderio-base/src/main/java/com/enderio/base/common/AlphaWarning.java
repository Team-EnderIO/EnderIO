package com.enderio.base.common;

import com.enderio.EnderIOBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID, value = Dist.CLIENT)
public class AlphaWarning {
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().sendSystemMessage(Component
            .literal("[Warning] Ender IO is in alpha - expect bugs and take backups often!")
            .withStyle(ChatFormatting.YELLOW));
    }
}
