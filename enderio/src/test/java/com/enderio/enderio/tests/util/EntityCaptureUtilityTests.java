package com.enderio.enderio.tests.util;

import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.foundation.util.EntityCaptureUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class EntityCaptureUtilityTests {
    // It appears JUnit tests only load data from the test resources directory.

    @Test
    public void testGetCapturableStatus_RejectNonSerializable(MinecraftServer server) {
        Assertions.assertEquals(EntityCaptureUtils.CapturableStatus.INCOMPATIBLE, EntityCaptureUtils.getCapturableStatus(EntityType.PLAYER));
    }

    @Test
    public void testGetCapturableStatus_RejectBlacklisted(MinecraftServer server) {
        Assertions.assertEquals(EntityCaptureUtils.CapturableStatus.BLACKLISTED, EntityCaptureUtils.getCapturableStatus(EntityType.WARDEN));
    }

    @Test
    public void testGetCapturableStatus_AllowWhitelisted(MinecraftServer server) {
        // Ensure the test datapack is working
        Assertions.assertTrue(EntityType.ZOMBIE.is(EIOTags.EntityTypes.SOUL_VIAL_BLACKLIST));
        Assertions.assertTrue(EntityType.ZOMBIE.is(EIOTags.EntityTypes.SOUL_VIAL_WHITELIST));

        // Ensure whitelist has precedence
        Assertions.assertEquals(EntityCaptureUtils.CapturableStatus.CAPTURABLE, EntityCaptureUtils.getCapturableStatus(EntityType.ZOMBIE));
    }
}
