package com.enderio.base.tests.util;

import com.enderio.base.common.util.ExperienceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExperienceUtilityTests {

    @Test
    public void testGetXpNeededForNextLevel() {
        // https://minecraft.wiki/w/Experience#Leveling_up
        Assertions.assertEquals(7, ExperienceUtil.getXpNeededForNextLevel(0));
        Assertions.assertEquals(15, ExperienceUtil.getXpNeededForNextLevel(4));
        Assertions.assertEquals(52, ExperienceUtil.getXpNeededForNextLevel(18));
        Assertions.assertEquals(265, ExperienceUtil.getXpNeededForNextLevel(47));
    }

    @Test
    public void testGetTotalXpFromLevel() {
        // https://minecraft.wiki/w/Experience#Leveling_up
        Assertions.assertEquals(112, ExperienceUtil.getTotalXpFromLevel(8));
        Assertions.assertEquals(493, ExperienceUtil.getTotalXpFromLevel(19));
        Assertions.assertEquals(4020, ExperienceUtil.getTotalXpFromLevel(45));
    }

    @Test
    public void testGetTotalLevelFromXp() {
        // https://minecraft.wiki/w/Experience#Leveling_up
        Assertions.assertEquals(8, ExperienceUtil.getTotalLevelFromXp(112));
        Assertions.assertEquals(19, ExperienceUtil.getTotalLevelFromXp(493));
        Assertions.assertEquals(45, ExperienceUtil.getTotalLevelFromXp(4020));
    }

}
