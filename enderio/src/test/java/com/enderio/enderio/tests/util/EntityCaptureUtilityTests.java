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
    public void testGetCapturableStatus_RejectDenied(MinecraftServer server) {
        Assertions.assertEquals(EntityCaptureUtils.CapturableStatus.DENIED, EntityCaptureUtils.getCapturableStatus(EntityType.WARDEN));
    }

    @Test
    public void testGetCapturableStatus_PermitAllowListed(MinecraftServer server) {
        // Ensure the test datapack is working
        Assertions.assertTrue(EntityType.ZOMBIE.builtInRegistryHolder().is(EIOTags.EntityTypes.SOUL_VIAL_DENY_LIST));
        Assertions.assertTrue(EntityType.ZOMBIE.builtInRegistryHolder().is(EIOTags.EntityTypes.SOUL_VIAL_ALLOY_LIST));

        // Ensure whitelist has precedence
        Assertions.assertEquals(EntityCaptureUtils.CapturableStatus.CAPTURABLE, EntityCaptureUtils.getCapturableStatus(EntityType.ZOMBIE));
    }
}
