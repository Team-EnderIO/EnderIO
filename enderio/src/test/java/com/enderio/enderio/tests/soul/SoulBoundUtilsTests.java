package com.enderio.enderio.tests.soul;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.world.entity.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SoulBoundUtilsTests {
    // Regression test for GH-1174
    @Test
    public void testGetBoundIfCapableNoModifyInput() {
        // Ensures that getBoundIfCapable doesn't mutate the input
        var originalStack = EIOItems.BROKEN_SPAWNER.get().getDefaultInstance();
        var optionalResult = SoulBoundUtils.getBoundIfCapable(originalStack, Soul.of(EntityType.COW));
        Assertions.assertTrue(optionalResult.isPresent());
        Assertions.assertNotSame(originalStack, optionalResult.get());
    }
}
