package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DarkSteelSwordItem extends SwordItem implements IAdvancedTooltipProvider {
    public DarkSteelSwordItem(Properties pProperties) {
        super(ArmoryItems.DARK_STEEL_TIER, 3, -2.4F, pProperties);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (pTarget.isDeadOrDying() && pTarget.level().random.nextFloat() < 0.07) {
            Optional<ItemStack> skull = getSkull(pTarget);
            skull.ifPresent(itemStack ->
                Containers.dropItemStack(pAttacker.level(), pAttacker.position().x,
                    pAttacker.position().y, pAttacker.position().z, itemStack)
            );
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    //TODO Quick and dirty. Not using instanceof cause of possible mod oddities
    public static Optional<ItemStack> getSkull(LivingEntity pTarget) {
        if (pTarget.getType() == EntityType.SKELETON || pTarget.getType() == EntityType.STRAY) {
            return Optional.of(new ItemStack(Items.SKELETON_SKULL));
        }
        if (pTarget.getType() == EntityType.ZOMBIE || pTarget.getType() == EntityType.DROWNED || pTarget.getType() == EntityType.HUSK || pTarget.getType() == EntityType.ZOMBIE_VILLAGER) {
            return Optional.of(new ItemStack(Items.ZOMBIE_HEAD));
        }
        if (pTarget.getType() == EntityType.WITHER_SKELETON) {
            return Optional.of(new ItemStack(Items.WITHER_SKELETON_SKULL));
        }
        if (pTarget.getType() == EntityType.CREEPER) {
            return Optional.of(new ItemStack(Items.CREEPER_HEAD));
        }
        if (pTarget.getType() == EntityType.ENDER_DRAGON) {
            return Optional.of(new ItemStack(Items.DRAGON_HEAD));
        }
        if (pTarget.getType() == EntityType.ENDERMAN) {
            return Optional.of(new ItemStack(EIOBlocks.ENDERMAN_HEAD));
        }
        if (pTarget.getType() == EntityType.PIGLIN || pTarget.getType() == EntityType.PIGLIN_BRUTE || pTarget.getType() == EntityType.ZOMBIFIED_PIGLIN) {
            return Optional.of(new ItemStack(Items.PIGLIN_HEAD));
        }
        if (pTarget instanceof Player player) {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            CompoundTag compoundtag = stack.getOrCreateTag();
            compoundtag.putString(PlayerHeadItem.TAG_SKULL_OWNER, player.getDisplayName().getString());
            return Optional.of(stack);
        }
        return Optional.empty();
    }

    //TODO remove when doing tools
    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(Component.literal("This item is currently only used to get mob heads"));
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(TooltipUtil.withArgs(ArmoryLang.HEAD_DROP_CHANCE, 7));
    }
}
