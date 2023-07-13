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
    public static Vector2i getLevelForExpWithLeftover(int experience, int startLevel, int stopLevel) {
        int expNeeded = getXpNeededForNextLevel(startLevel);
        if (expNeeded < experience && startLevel < stopLevel) {
            experience -= expNeeded;
            startLevel +=1;
            return getLevelForExpWithLeftover(experience, startLevel, stopLevel);
        }
        return new Vector2i(startLevel, experience);
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
     * Get the Experience amount from a level.
     * @param level
     * @return
     */
    public static int getExpFromLevel(int level) {
        int leftover = getLevelForExpWithLeftover(Integer.MAX_VALUE, 0, level).y();
        return Integer.MAX_VALUE - leftover;
    }

    /**
     * Get the fluid amount from a level.
     * @param level
     * @return
     */
    public static int getFluidFromLevel(int level) {
        return getExpFromLevel(level)* EXP_TO_FLUID;
    }

    public static int getPlayerTotalXp(Player player) {
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
