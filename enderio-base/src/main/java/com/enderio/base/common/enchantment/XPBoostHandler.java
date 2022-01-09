package com.enderio.base.common.enchantment;

import com.enderio.base.common.init.EIOEnchantments;
import com.google.common.base.Throwables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;

@EventBusSubscriber
public class XPBoostHandler {
    private static final Method getExperiencePoints = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_6552_", Player.class);
    private static final @Nonnull String NBT_KEY = "enderio:xpboost";

    @SubscribeEvent
    public static void handleEntityKill(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Entity killer = event.getSource().getDirectEntity();

        if (!entity.level.isClientSide && killer != null) {
            if (killer instanceof Player player) {
                scheduleXP(entity, getXPBoost(entity, player));
            } else if (killer instanceof Arrow arrow) {
                CompoundTag tag = killer.getPersistentData();
                if (tag.contains(NBT_KEY) && tag.getInt(NBT_KEY) >= 0) {
                    int level = tag.getInt(NBT_KEY);
                    scheduleXP(entity, getXPBoost(entity, (Player) arrow.getOwner(), level));
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleArrowFire(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            arrow.getPersistentData().putInt(NBT_KEY, getXPBoostLevel(arrow.getOwner()));
        }
    }

    @SubscribeEvent
    public static void handleBlockBreak(BlockEvent.BreakEvent event) {
        int level = getXPBoostLevel(event.getPlayer());

        if (level >= 0) {
            BlockState state = event.getState();
            Level world = (Level) event.getWorld();
            BlockPos pos = event.getPos();
            final int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, event.getPlayer().getMainHandItem());
            final int xp = state.getBlock().getExpDrop(state, world, pos, fortune, 0);
            if (xp > 0) {
                world.addFreshEntity(new ExperienceOrb(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, getXPBoost(xp, level)));
            }
        }
    }

    private static int getXPBoost(LivingEntity killed, Player player) {
        return getXPBoost(killed, player, getXPBoostLevel(player));
    }

    private static int getXPBoost(LivingEntity killed, Player player, int level) {
        if (level >= 0) {
            try {
                int xp = (Integer) getExperiencePoints.invoke(killed, player);
                return getXPBoost(xp, level);
            } catch (Exception e) {
                Throwables.propagate(e);
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

    private static void scheduleXP(Entity entity, int boost) {
        scheduleXP(entity.level, entity.getX(), entity.getY(), entity.getZ(), boost);
    }

    private static void scheduleXP(Level world, double x, double y, double z, int boost) {
        if (boost > 0)
            world.addFreshEntity(new ExperienceOrb(world, x, y, z, boost));
    }
}
