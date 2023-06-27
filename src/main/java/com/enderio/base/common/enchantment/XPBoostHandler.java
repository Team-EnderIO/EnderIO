package com.enderio.base.common.enchantment;

import com.enderio.base.common.init.EIOEnchantments;
import com.google.common.base.Throwables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Map;

@EventBusSubscriber
public class XPBoostHandler {
    private static final String NBT_KEY = "EnderIOXpBoostLevel";

    @SubscribeEvent
    public static void handleExperienceDropEvent(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() != null) {
            DamageSource lastDamageSource = event.getEntity().getLastDamageSource();

            int xpBoost = 0;
            if (lastDamageSource != null && lastDamageSource.getDirectEntity() instanceof Arrow arrow) {
                CompoundTag tag = arrow.getPersistentData();
                if (tag.contains(NBT_KEY) && tag.getInt(NBT_KEY) >= 0) {
                    int level = tag.getInt(NBT_KEY);

                    // This makes the safe assumption that the attacking player is the one who dealt the last bow shot.
                    xpBoost = getXPBoost(event.getEntity(), level);
                }
            } else {
                xpBoost = getXPBoost(event.getEntity(), event.getAttackingPlayer());
            }

            if (xpBoost > 0) {
                event.setDroppedExperience(event.getDroppedExperience() + xpBoost);
            }
        }
    }

    @SubscribeEvent
    public static void handleArrowFire(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.getOwner() != null) {
            arrow.getPersistentData().putInt(NBT_KEY, getXPBoostLevel(arrow.getOwner()));
        }
    }

    @SubscribeEvent
    public static void handleBlockBreak(BlockEvent.BreakEvent event) {
        int boostLevel = getXPBoostLevel(event.getPlayer());

        if (boostLevel >= 0) {
            BlockState state = event.getState();
            Level level = (Level) event.getLevel();
            BlockPos pos = event.getPos();
            final int fortune = event.getPlayer().getMainHandItem().getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
            final int xp = state.getBlock().getExpDrop(state, level, RandomSource.create(), pos, fortune, 0);
            if (xp > 0) {
                level.addFreshEntity(new ExperienceOrb(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, getXPBoost(xp, boostLevel)));
            }
        }
    }

    private static int getXPBoost(LivingEntity killed, Player player) {
        return getXPBoost(killed, getXPBoostLevel(player));
    }

    private static int getXPBoost(LivingEntity killed, int level) {
        if (level >= 0) {
            try {
                int xp = killed.getExperienceReward();
                return getXPBoost(xp, level);
            } catch (Exception e) {
                Throwables.throwIfUnchecked(e);
            }
        }
        return 0;
    }

    private static int getXPBoost(int xp, int level) {
        return Math.round(xp * ((float) Math.log10(level + 1) * 2));
    }

    private static int getXPBoostLevel(Entity entity) {
        if (entity instanceof Player player && !(player instanceof FakePlayer)) {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.isEmpty()) {
                return -1;
            }

            int result = -1;

            for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(weapon).entrySet()) {
                if (entry.getKey() == Enchantments.SILK_TOUCH) {
                    return -1;
                }
                if (entry.getKey() == EIOEnchantments.XP_BOOST.get()) {
                    result = entry.getValue();
                }
            }
            return result;
        }
        return -1;
    }
}
