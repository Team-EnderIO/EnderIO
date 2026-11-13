package com.enderio.enderio.content.armory;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.api.armory.MobHeadDrop;
import com.enderio.enderio.api.armory.WeaponBeheading;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber
public class BeheadingHandler {
    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event) {
        ItemStack weapon = event.getSource().getWeaponItem();
        if (weapon == null) {
            return;
        }

        WeaponBeheading beheading = weapon.get(EnderIODataComponents.WEAPON_BEHEADING);
        if (beheading == null) {
            return;
        }

        Entity entity = event.getEntity();
        List<ItemStack> possibleHeads;

        if (entity instanceof Player player) {
            // TODO: This stops datapacks adding other heads to the player, but I think its fine?
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            stack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
            possibleHeads = List.of(stack);
        } else {
            //noinspection deprecation
            possibleHeads = entity.getType().builtInRegistryHolder().getData(MobHeadDrop.DATA_MAP);
        }

        if (possibleHeads == null || possibleHeads.isEmpty()) {
            return;
        }

        Level level = entity.level();
        if (level.getRandom().nextFloat() >= beheading.headDropChance()) {
            return;
        }

        // Pick a random head, and drop it.
        int selectedHeadIndex = level.getRandom().nextInt(0, possibleHeads.size());
        ItemStack headResult = possibleHeads.get(selectedHeadIndex);
        event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), headResult.copy()));
    }

    @SubscribeEvent
    public static void addTooltipHint(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        WeaponBeheading beheadingConfig = stack.get(EnderIODataComponents.WEAPON_BEHEADING);
        if (beheadingConfig == null) {
            return;
        }

        event.getToolTip().add(TooltipUtil.withArgs(ArmoryLang.ENDER_HEAD_DROP_CHANCE, Math.round(beheadingConfig.headDropChance() * 100)));
    }
}
