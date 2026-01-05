package com.enderio.enderio.content.tools;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.foundation.util.AttractionUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ElectromagnetItem extends PoweredToggledItem {

    private static final double COLLISION_DISTANCE_SQ = 1.25 * 1.25;
    private static final double SPEED = 0.035;
    private static final double SPEED_4 = SPEED * 4;

    public ElectromagnetItem(Properties properties) {
        super(properties);
    }

    @Override
    protected int getEnergyUse() {
        return BaseConfig.COMMON.ITEMS.ELECTROMAGNET_ENERGY_USE.get();
    }

    @Override
    public int getMaxEnergy() {
        return BaseConfig.COMMON.ITEMS.ELECTROMAGNET_MAX_ENERGY.get();
    }

    private int getRange() {
        return BaseConfig.COMMON.ITEMS.ELECTROMAGNET_RANGE.get();
    }

    private int getMaxItems() {
        return BaseConfig.COMMON.ITEMS.ELECTROMAGNET_MAX_ITEMS.get();
    }

    private boolean isBlacklisted(ItemEntity entity) {
        return entity.getItem().is(EIOTags.Items.ELECTROMAGNET_BLACKLIST);
    }

    private boolean isMagnetable(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return !isBlacklisted(itemEntity);
        }
        return entity instanceof ExperienceOrb;
    }

    @Override
    protected void onTickWhenActive(Player player, ItemStack stack, Level level, @Nullable EquipmentSlot slot) {
        if (player.isSpectator()) {
            return;
        }

        int range = getRange();
        AABB bounds = new AABB(player.getX() - range, player.getY() - range, player.getZ() - range,
            player.getX() + range, player.getY() + range, player.getZ() + range);

        List<Entity> toMove = level.getEntities(player, bounds, this::isMagnetable);

        int itemsRemaining = getMaxItems();
        if (itemsRemaining <= 0) {
            itemsRemaining = Integer.MAX_VALUE;
        }

        for (Entity entity : toMove) {
            if (AttractionUtil.moveToPos(entity, player.getX(), player.getY() + player.getEyeHeight() * .75f,
                player.getZ(), SPEED, SPEED_4, COLLISION_DISTANCE_SQ)) {
                entity.playerTouch(player);

            }

            if (itemsRemaining-- <= 0) {
                return;
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
