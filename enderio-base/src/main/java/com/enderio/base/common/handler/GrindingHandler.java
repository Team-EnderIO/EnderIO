package com.enderio.base.common.handler;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID)
public class GrindingHandler {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //Server Side Only
        if (!event.getSide().isServer()) {
            return;
        }

        Player player = event.getEntity();
        if (!player.isCrouching()) {
            return;
        }

        BlockState target = event.getLevel().getBlockState(event.getPos());
        if (!(target.is(Blocks.GRINDSTONE) || target.is(Tags.Blocks.OBSIDIANS) || target.is(Blocks.CRYING_OBSIDIAN))) {
            return;
        }

        ItemStack mainhand = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(Items.FLINT)) {
            return;
        }

        if (mainhand.is(Items.DEEPSLATE) || mainhand.is(Items.COBBLED_DEEPSLATE)) {
            mainhand.shrink(1);
            // Grindstone consumes Flint 33% of the time
            if (target.is(Blocks.GRINDSTONE) && RandomSource.create().nextInt(3) == 0) {
                offhand.shrink(1);
            }
            int output = 1;
            // Crying Obsidian has a 33% chance to double the output
            if (target.is(Blocks.CRYING_OBSIDIAN) && RandomSource.create().nextInt(3) == 0) {
                output = 2;
            }
            player.addItem(new ItemStack(EIOItems.GRAINS_OF_INFINITY.get(), output));
            event.setCanceled(true);
        } else if (mainhand.is(Items.COAL) && mainhand.getCount() >= 3) {
            int cost = 3;
            // Crying Obsidian had a 33% chance to only consume 1 coal instead of 3
            if (target.is(Blocks.CRYING_OBSIDIAN) && RandomSource.create().nextInt(3) == 0) {
                cost = 1;
            }
            mainhand.shrink(cost);
            // Grindstone consumes Flint 33% of the time
            if (target.is(Blocks.GRINDSTONE) && RandomSource.create().nextInt(3) == 0) {
                offhand.shrink(1);
            }
            player.addItem(new ItemStack(EIOItems.POWDERED_COAL.get(), 1));
            event.setCanceled(true);
        }
    }
}
