package com.enderio.enderio.content.armory;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DarkSteelSwordItem extends SwordItem implements AdvancedTooltipProvider, IDarkSteelItem {
    public DarkSteelSwordItem(Properties properties) {
        super(DarkSteelTiers.DARK_STEEL_TIER,
                properties.attributes(createAttributes(DarkSteelTiers.DARK_STEEL_TIER, 3, -2.4F))
                        .component(EIODataComponents.TRAVEL_ITEM, false));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Temporary head drop logic
        if (target.isDeadOrDying() && target.level().random.nextFloat() < 0.07) {
            Optional<ItemStack> skull = getSkull(target);
            skull.ifPresent(itemStack -> Containers.dropItemStack(attacker.level(), attacker.position().x,
                attacker.position().y, attacker.position().z, itemStack));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    private static Optional<ItemStack> getSkull(LivingEntity target) {
        if (target.getType() == EntityType.SKELETON || target.getType() == EntityType.STRAY) {
            return Optional.of(new ItemStack(Items.SKELETON_SKULL));
        }
        if (target.getType() == EntityType.ZOMBIE || target.getType() == EntityType.DROWNED
                || target.getType() == EntityType.HUSK || target.getType() == EntityType.ZOMBIE_VILLAGER) {
            return Optional.of(new ItemStack(Items.ZOMBIE_HEAD));
        }
        if (target.getType() == EntityType.WITHER_SKELETON) {
            return Optional.of(new ItemStack(Items.WITHER_SKELETON_SKULL));
        }
        if (target.getType() == EntityType.CREEPER) {
            return Optional.of(new ItemStack(Items.CREEPER_HEAD));
        }
        if (target.getType() == EntityType.ENDER_DRAGON) {
            return Optional.of(new ItemStack(Items.DRAGON_HEAD));
        }
        if (target.getType() == EntityType.ENDERMAN) {
            return Optional.of(new ItemStack(EIOBlocks.ENDERMAN_HEAD));
        }
        if (target.getType() == EntityType.PIGLIN || target.getType() == EntityType.PIGLIN_BRUTE
                || target.getType() == EntityType.ZOMBIFIED_PIGLIN) {
            return Optional.of(new ItemStack(Items.PIGLIN_HEAD));
        }
        if (target instanceof Player player) {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            stack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
            return Optional.of(stack);
        }
        return Optional.empty();
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(Component.literal("This item is currently only used to get mob heads"));
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(TooltipUtil.withArgs(ArmoryLang.ENDER_HEAD_DROP_CHANCE, 7));
    }
}
