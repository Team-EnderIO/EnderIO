package com.enderio.base.common.handler;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class GrindingHandler {
    @SubscribeEvent
    public static void onGrindstonePlace(GrindstoneEvent.OnPlaceItem event) {
        if ((event.getTopItem().is(Items.DEEPSLATE) || event.getTopItem().is(Items.COBBLED_DEEPSLATE)) && event.getBottomItem().is(Items.FLINT)) {
            event.setOutput(new ItemStack(EIOItems.GRAINS_OF_INFINITY.get(), 1));
            event.setXp(0);
        } else if (event.getTopItem().is(Items.COAL) && event.getTopItem().getCount() >= 3 && event.getBottomItem().is(Items.FLINT)) {
            event.setOutput(new ItemStack(EIOItems.POWDERED_COAL.get(), 1));
            event.setXp(0);
        }
    }

    @SubscribeEvent
    public static void onGrindstoneResult(GrindstoneEvent.OnTakeItem event) {
        if ((event.getTopItem().is(Items.DEEPSLATE) || event.getTopItem().is(Items.COBBLED_DEEPSLATE)) && event.getBottomItem().is(Items.FLINT)) {
            ItemStack top = event.getTopItem().copy();
            ItemStack bottom = event.getBottomItem().copy();

            top.shrink(1);

            // 33% chance to destroy the flint
            if (RandomSource.create().nextInt(3) == 0) {
                bottom.shrink(1);
            }

            event.setNewTopItem(top);
            event.setNewBottomItem(bottom);
        } else if (event.getTopItem().is(Items.COAL) && event.getTopItem().getCount() >= 3 && event.getBottomItem().is(Items.FLINT)) {
            ItemStack top = event.getTopItem().copy();
            ItemStack bottom = event.getBottomItem().copy();

            top.shrink(3);

            // 33% chance to destroy the flint
            if (RandomSource.create().nextInt(3) == 0) {
                bottom.shrink(1);
            }

            event.setNewTopItem(top);
            event.setNewBottomItem(bottom);
        }
    }
}
