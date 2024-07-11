package com.enderio.base.common.block;

import com.enderio.EnderIOBase;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIOBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID, value = Dist.CLIENT)
public class DarkSteelLadderHandler {

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Pre playerTickEvent) {
        if (playerTickEvent.getEntity() == Minecraft.getInstance().player) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player.onClimbable() && player.level().getBlockState(player.blockPosition()).is(EIOBlocks.DARK_STEEL_LADDER.get())) {
                if (!Minecraft.getInstance().options.keyShift.isDown()) {
                    if (Minecraft.getInstance().options.keyUp.isDown()) {
                        player.move(MoverType.SELF, new Vec3(0, BaseConfig.COMMON.BLOCKS.DARK_STEEL_LADDER_BOOST.get(),0));
                    } else {
                        player.move(MoverType.SELF, new Vec3(0,-BaseConfig.COMMON.BLOCKS.DARK_STEEL_LADDER_BOOST.get(),0));
                    }
                }
            }
        }
    }
}
