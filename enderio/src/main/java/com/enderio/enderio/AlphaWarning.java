package com.enderio.enderio;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class AlphaWarning {
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().sendSystemMessage(Component
            .literal("[Warning] Ender IO is in alpha - expect bugs and take backups often!")
            .withStyle(ChatFormatting.YELLOW));
    }
}
