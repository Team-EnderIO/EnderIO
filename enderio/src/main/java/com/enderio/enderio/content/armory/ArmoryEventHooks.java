//package com.enderio.enderio.content.armory;
//
//import net.minecraft.world.entity.player.Player;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.event.tick.PlayerTickEvent;
//
//@EventBusSubscriber
//public class ArmoryEventHooks {
//    @SubscribeEvent
//    public static void onPlayerTick(PlayerTickEvent.Pre event) {
//        Player player = event.getEntity();
//        if (player.level().isClientSide()) {
//            return;
//        }
//    }
//}
