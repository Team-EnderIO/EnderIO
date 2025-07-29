package com.enderio.base.common.util;

import net.minecraft.world.entity.player.Player;

public class ExperienceUtil {

    // 1 exp = 20 mb
    public static int EXP_TO_FLUID = 20;

    /**
     * Vanilla way of calculating experience points required for level up.
     * @param currentLevel - the current level of player. The level up cost depends on currentLevel and not on the level you are trying to reach
     * @return experience - experience cost to level up
     */

    public static int getXpNeededForNextLevel(int currentLevel) {
        if (currentLevel >= 30) {
            return 112 + (currentLevel - 30) * 9;
        } else {
            return currentLevel >= 15 ? 37 + (currentLevel - 15) * 5 : 7 + currentLevel * 2;
        }
    }

    /**
     * Returns the total xp required to reach the target level ( from 0 )
     * @param level - level to reach
     * @return experience - total xp cost
     */
    public static long getTotalXpFromLevel(int level) {
        if (level >= 32) {
            return (long) ((4.5 * Math.pow(level, 2)) - 162.5 * level + 2220);
        } else if (level >= 17) {
            return (long) ((2.5 * Math.pow(level, 2)) - 40.5 * level + 360);
        } else {
            return (long) (Math.pow(level, 2) + 6L * level);
        }
    }

    /**
     * Returns the max level attainable using all of the experience
     * @param experience - total experience
     * @return level - max level
     */
    public static int getTotalLevelFromXp(long experience) {
        if (experience >= 1508) {
            return (int) ((325.0 / 18) + (Math.sqrt(( 2.0 /9) * (experience - (54215.0 / 72)))));
        } else if (experience >= 353) {
            return (int) ((81.0 / 10) + (Math.sqrt(( 2.0 /5) * (experience - (7839.0 / 40)))));
        } else {
            return (int) (Math.sqrt(experience + 9) - 3);
        }
    }

    public static long getPlayerTotalXp(Player player) {
        return getTotalXpFromLevel(player.experienceLevel) + (long) Math.floor(player.experienceProgress * getXpNeededForNextLevel(player.experienceLevel));
    }

    /**
     * Fills the experience bar just like a player, starting from a certain level, with a possible maximum.
     * Returns the level it reached and the leftover exp.
     * @param experience
     * @param startLevel
     * @param stopLevel
     * @return
     */
    public static ExperienceLevel getLevelForExpWithLeftover(int experience, int startLevel, int stopLevel) {
        long startXp = getTotalXpFromLevel(startLevel);
        long totalXp = startXp + experience;
        int maxLevel = Math.min(getTotalLevelFromXp(totalXp), stopLevel);

        int remainder = (int) (totalXp - getTotalXpFromLevel(maxLevel));
        return new ExperienceLevel(maxLevel, remainder);
    }

    /**
     * Gets the level and amount of experience leftover using a fluid. With start and max level.
     * @param fluidAmount
     * @param startLevel
     * @param stopLevel
     * @return experienceLevel
     */
    public static ExperienceLevel getLevelFromFluidWithLeftover(int fluidAmount, int startLevel, int stopLevel) {
        ExperienceLevel res = getLevelForExpWithLeftover(fluidAmount/ EXP_TO_FLUID, startLevel, stopLevel);
        return new ExperienceLevel(res.level(), res.experience());
    }

    /**
     * Gets the level and amount of experience leftover using a fluid.
     * Starts from 0
     * @param fluidAmount
     * @return
     */
    public static ExperienceLevel getLevelFromFluidWithLeftover(int fluidAmount) {
        return getLevelFromFluidWithLeftover(fluidAmount, 0, Integer.MAX_VALUE);
    }

    /**
     * Gets the level using a fluid.
     * Starts from 0
     * @param fluidAmount
     * @return
     */
    public static int getLevelFromFluid(int fluidAmount) {
        return getLevelFromFluidWithLeftover(fluidAmount).level();
    }

    /**
     * Get the fluid amount from a level.
     * @param level
     * @return
     */
    public static long getFluidFromLevel(int level) {
        return getTotalXpFromLevel(level) * EXP_TO_FLUID;
    }

    public record ExperienceLevel(int level, int experience){}

}
