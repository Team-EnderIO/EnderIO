package com.enderio.enderio.content.decor;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DarkSteelLadderHandler {

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.player == Minecraft.getInstance().player) {
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

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().is(EIOBlocks.DARK_STEEL_BLOCK.get().asItem())) {
            event.getToolTip().add(EIOCommonLang.DARK_STEEL_LADDER_FASTER);
        }
    }
}
