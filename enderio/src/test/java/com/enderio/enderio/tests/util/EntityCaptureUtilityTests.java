package com.enderio.enderio.tests.util;

import com.enderio.enderio.common.foundation.tag.EIOTags;
import com.enderio.enderio.common.foundation.util.EntityCaptureUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

@ExtendWith(EphemeralTestServerProvider.class)
public class EntityCaptureUtilityTests {
    // It appears JUnit tests only load data from the test resources directory.

    @Test
    public void testIsBlacklistedBoss(MinecraftServer server) {
        // Get all bosses
        var entityTypeRegistry = server.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        var bossesTag = entityTypeRegistry.getTag(Tags.EntityTypes.BOSSES).orElseThrow();

        Assertions.assertAll(bossesTag.stream()
            .map((bossEntityType) ->
                (Executable) () -> Assertions.assertTrue(EntityCaptureUtils.isBlacklistedBoss(bossEntityType.value())))
            .toList());
    }

    @Test
    public void testGetCapturableStatus_RejectNonSerializable(MinecraftServer server) {
        Assertions.assertEquals(EntityCaptureUtils.CapturableStatus.INCOMPATIBLE, EntityCaptureUtils.getCapturableStatus(EntityType.PLAYER));
    }

    @Test
    public void testGetCapturableStatus_RejectBoss(MinecraftServer server) {
        Assertions.assertEquals(EntityCaptureUtils.CapturableStatus.BOSS, EntityCaptureUtils.getCapturableStatus(EntityType.ENDER_DRAGON));
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
