package com.enderio.base.common.handler;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class GrindingHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Block target = event.getLevel().getBlockState(event.getPos()).getBlock();
        ItemStack mainhand = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();
        if (!(player.isCrouching())
            || (target != Blocks.OBSIDIAN && target != Blocks.GRINDSTONE && target != Blocks.CRYING_OBSIDIAN)
            || !(offhand.is(Items.FLINT))) {
            return;
        }
        if (mainhand.is(Items.DEEPSLATE) || mainhand.is(Items.COBBLED_DEEPSLATE)) {
            mainhand.shrink(1);
            // Grindstone consumes Flint 33% of the time
            if (target != Blocks.OBSIDIAN && target != Blocks.CRYING_OBSIDIAN && RandomSource.create().nextInt(3) == 0) {
                offhand.shrink(1);
            }
            int output = 1;
            // Crying Obsidian has a 33% chance to double the output
            if (target == Blocks.CRYING_OBSIDIAN && RandomSource.create().nextInt(3) == 0) {
                output = 2;
            }
            player.addItem(new ItemStack(EIOItems.GRAINS_OF_INFINITY.get(), output));
            event.setCanceled(true);
        } else if (mainhand.is(Items.COAL) && mainhand.getCount() >= 3) {
            int cost = 3;
            // Crying Obsidian had a 33% chance to only consume 1 coal instead of 3
            if (target == Blocks.CRYING_OBSIDIAN && RandomSource.create().nextInt(3) == 0) {
                cost = 1;
            }
            mainhand.shrink(cost);
            // Grindstone consumes Flint 33% of the time
            if (target != Blocks.OBSIDIAN && target != Blocks.CRYING_OBSIDIAN && RandomSource.create().nextInt(3) == 0) {
                offhand.shrink(1);
            }
            player.addItem(new ItemStack(EIOItems.POWDERED_COAL.get(), 1));
        }
        // Cancel the event so that we don't bring up the GUI for the grindstone. Unnecessary for obsidian
        if (target == Blocks.GRINDSTONE) {
            event.setCanceled(true);
        }
    }
}
