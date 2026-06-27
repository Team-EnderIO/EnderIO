package com.enderio.enderio.tests.api.soul;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class SoulBoundUtilsTests {
    // Regression test for GH-1174
    @Test
    public void testGetBoundIfCapableNoModifyInput(MinecraftServer server) {
        // Ensures that getBoundIfCapable doesn't mutate the input
        var originalStack = EIOItems.BROKEN_SPAWNER.get().getDefaultInstance();
        var optionalResult = SoulBoundUtils.getBoundIfCapable(originalStack, Soul.of(EntityTypes.COW));
        Assertions.assertTrue(optionalResult.isPresent());
        Assertions.assertNotSame(originalStack, optionalResult.get());
    }
}
