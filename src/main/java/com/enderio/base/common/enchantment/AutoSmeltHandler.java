package com.enderio.base.common.enchantment;

import com.enderio.base.common.init.EIOEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

@EventBusSubscriber
public class AutoSmeltHandler {

    @SubscribeEvent
    public static void handleBlockBreak(BlockEvent.BreakEvent event) {
        // Checks if running on server and enchant is on tool
        if (!event.getLevel().isClientSide()
            && event.getPlayer().getMainHandItem().getEnchantmentLevel(EIOEnchantments.AUTO_SMELT.get()) > 0) {
            ServerLevel serverWorld = ((ServerLevel) event.getLevel()); // Casts IWorld to ServerWorld

            LootParams.Builder lootparamsBuilder = (new LootParams.Builder(serverWorld))
                .withParameter(LootContextParams.THIS_ENTITY, event.getPlayer())
                .withParameter(LootContextParams.ORIGIN, event.getPos().getCenter())
                .withParameter(LootContextParams.TOOL, event.getPlayer().getMainHandItem());

            // to calculate drops
            List<ItemStack> drops = event.getState().getDrops(lootparamsBuilder); // Calculates drops
            for (ItemStack item : drops) { // Iteration
                ItemStack stack = serverWorld
                    .getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(item), serverWorld)
                    .map(r -> r.assemble(new SimpleContainer(item), event.getLevel().registryAccess()))
                    .filter(itemStack -> !itemStack.isEmpty())
                    .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, item.getCount() * itemStack.getCount()))
                    .orElse(item); // Recipe as var
                Containers.dropItemStack(event.getPlayer().level(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
            }
            event.getPlayer().level().removeBlock(event.getPos(), false); // Breaks block
            event.setResult(Event.Result.DENY);
        }
    }
}
