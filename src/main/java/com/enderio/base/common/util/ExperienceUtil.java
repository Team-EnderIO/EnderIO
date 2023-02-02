package com.enderio.base.common.util;

import com.enderio.core.common.util.Vector2i;

public class ExperienceUtil {

    public static Vector2i getLevelForExpWithLeftover(int experience, int startLevel, int stopLevel) {
        int expNeeded = getXpNeededForNextLevel(startLevel);
        if (expNeeded < experience && startLevel < stopLevel) {
            experience -= expNeeded;
            startLevel +=1;
            return getLevelForExpWithLeftover(experience, startLevel, stopLevel);
        }
        return new Vector2i(startLevel, experience);
    }

    public static Vector2i getLevelFromFluidWithLeftover(int fluidAmount, int startLevel, int stopLevel) {
        return getLevelForExpWithLeftover(fluidAmount*24, startLevel, stopLevel);
    }

    public static Vector2i getLevelFromFluidWithLeftover(int fluidAmount) {
        return getLevelForExpWithLeftover(fluidAmount*24, 0, Integer.MAX_VALUE);
    }

    public static int getLevelFromFluid(int fluidAmount) {
        return getLevelFromFluidWithLeftover(fluidAmount).x();
    }

    public static int getLevelFromFluid(int fluidAmount, int startLevel, int stopLevel) {
        return getLevelFromFluidWithLeftover(fluidAmount, startLevel, stopLevel).x();
    }

    private static int getLevelForExp(int experience, int startLevel, int stopLevel) {
        return getLevelForExpWithLeftover(experience, startLevel, stopLevel).x();
    }

    public static int getXpNeededForNextLevel(int explevel) {
        if (explevel >= 30) {
            return 112 + (explevel - 30) * 9;
        } else {
            return explevel >= 15 ? 37 + (explevel - 15) * 5 : 7 + explevel * 2;
        }
    }
}
