package com.enderio.base.common.util;

import com.enderio.api.misc.Vector2i;
import net.minecraft.world.entity.player.Player;

public class ExperienceUtil {

    // 1 exp = 20 mb
    public static int EXP_TO_FLUID = 20;

    /**
     * Fills the experience bar just like a player, starting from a certain level, with a possible maximum.
     * Returns the level it reached and the leftover exp.
     * @param experience
     * @param startLevel
     * @param stopLevel
     * @return
     */
    public static Vector2i getLevelForExpWithLeftover(long experience, int startLevel, int stopLevel) {
        long startXp = getExpFromLevel(startLevel);
        long totalXp = startXp + experience;
        long xpNeededToStop = getExpFromLevel(stopLevel);
        if (totalXp >= xpNeededToStop) {
            int leftoverXP = (int) Math.min(totalXp - xpNeededToStop, Integer.MAX_VALUE);
            return new Vector2i(stopLevel, leftoverXP);
        }

        // Binary search
        while (startLevel < stopLevel) {
            int mid = (int) (startLevel + (stopLevel - startLevel + 1L) / 2);
            if (getExpFromLevel(mid) <= totalXp) {
                startLevel = mid;
            } else {
                stopLevel = mid - 1;
            }
        }

        int leftoverXP = (int) Math.min(totalXp - getExpFromLevel(startLevel), Integer.MAX_VALUE);
        return new Vector2i(startLevel, leftoverXP);
    }

    /**
     * Gets the level and amount of experience leftover using a fluid. With start and max level.
     * @param fluidAmount
     * @param startLevel
     * @param stopLevel
     * @return
     */
    public static Vector2i getLevelFromFluidWithLeftover(int fluidAmount, int startLevel, int stopLevel) {
        Vector2i res = getLevelForExpWithLeftover(fluidAmount/ EXP_TO_FLUID, startLevel, stopLevel);
        return res.add(fluidAmount % EXP_TO_FLUID, 0); //add leftover
    }

    /**
     * Gets the level and amount of experience leftover using a fluid.
     * Starts from 0
     * @param fluidAmount
     * @return
     */
    public static Vector2i getLevelFromFluidWithLeftover(int fluidAmount) {
        return getLevelForExpWithLeftover(fluidAmount/ EXP_TO_FLUID, 0, Integer.MAX_VALUE);
    }

    /**
     * Gets the level using a fluid.
     * Starts from 0
     * @param fluidAmount
     * @return
     */
    public static int getLevelFromFluid(int fluidAmount) {
        return getLevelFromFluidWithLeftover(fluidAmount).x();
    }

    /**
     * Gets the level using a fluid. With start and max level.
     * Starts from 0
     * @param fluidAmount
     * @return
     */
    public static int getLevelFromFluid(int fluidAmount, int startLevel, int stopLevel) {
        return getLevelFromFluidWithLeftover(fluidAmount, startLevel, stopLevel).x();
    }

    private static int getLevelForExp(int experience, int startLevel, int stopLevel) {
        return getLevelForExpWithLeftover(experience, startLevel, stopLevel).x();
    }

    /**
     * Vanilla way of calculating how much total xp is needed to reach given level
     *
     * @param level
     * @return
     */
    public static long getExpFromLevel(int level) {
        int maxLevel = 960383883; // to avoid overflow on 10 * (level * level)
        if (level > maxLevel) {
            return Long.MAX_VALUE; // consider throwing errors instead?
        }
        long lvl = level;
        if (lvl <= 14) {
            return (lvl * lvl) + (6L * lvl);
        }
        if (lvl <= 30) {
            return (10 * (lvl * lvl) - 162 * lvl + 1440) / 4;
        }
        lvl -= 30;
        return (9 * (lvl * lvl) + 215 * lvl + 2790) / 2;
    }

    /**
     * Get the fluid amount from a level.
     * @param level
     * @return
     */
    public static int getFluidFromLevel(int level) {
        return (int) Math.min(getExpFromLevel(level) * EXP_TO_FLUID, Integer.MAX_VALUE);
    }

    public static long getPlayerTotalXp(Player player) {
        return ExperienceUtil.getExpFromLevel(player.experienceLevel) +
            (int) Math.floor(player.experienceProgress * ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel + 1));
    }

    /**
     * Vanilla way of calculating levels from exp.
     * @param explevel
     * @return
     */
    public static int getXpNeededForNextLevel(int explevel) {
        if (explevel >= 30) {
            return 112 + (explevel - 30) * 9;
        } else {
            return explevel >= 15 ? 37 + (explevel - 15) * 5 : 7 + explevel * 2;
        }
    }
}
