package com.enderio.enderio.tests.api.soul;

import com.enderio.enderio.api.soul.Soul;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class SoulCodecTests {
    // Test CODEC vs OPTIONAL_CODEC

    private static final String NULL_FORMAT = "{}";
    private static final String NULL_ENTITY_TYPE = """
        {
            "entity_type": null,
            "entity_tag": {
                "Health": 10.0
            }
        }""";

    @Test
    public void testOptionalCodecWorks(MinecraftServer server) {
        // Parse json
        var json = JsonParser.parseString(NULL_FORMAT);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = Soul.OPTIONAL_CODEC.parse(ops, json);

        Assertions.assertTrue(result.isSuccess());

        var soul = result.getOrThrow();
        Assertions.assertNotNull(soul);
        Assertions.assertEquals(Soul.EMPTY, soul);
    }

    @Test
    public void testCodecNullFails(MinecraftServer server) {
        // Parse json
        var json = JsonParser.parseString(NULL_FORMAT);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = Soul.CODEC.parse(ops, json);

        Assertions.assertTrue(result.isError());
    }

    // This test is temporary, just to ensure that if any data has somehow saved with a null entity type, it resolves properly.
    @Test
    public void testOptionalCodecWithNullEntityTypeFails(MinecraftServer server) {
        // Parse json
        var json = JsonParser.parseString(NULL_ENTITY_TYPE);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = Soul.OPTIONAL_CODEC.parse(ops, json);

        Assertions.assertTrue(result.isSuccess());

        var soul = result.getOrThrow();
        Assertions.assertNotNull(soul);
        Assertions.assertEquals(Soul.EMPTY, soul);
    }

    @Test
    public void testCodecWithNullEntityTypeFails(MinecraftServer server) {
        // Parse json
        var json = JsonParser.parseString(NULL_ENTITY_TYPE);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = Soul.CODEC.parse(ops, json);

        Assertions.assertTrue(result.isError());
    }

    // 8.0.5+ format
    private static final String NEW_FORMAT = """
        {
            "entity_type": "minecraft:allay",
            "entity_tag": {
                "id": "minecraft:allay",
                "Health": 10.0
            }
        }
        """;

    @Test
    public void testLoadNewFormat(MinecraftServer server) {
        // Parse json
        var json = JsonParser.parseString(NEW_FORMAT);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = Soul.CODEC.parse(ops, json);

        Assertions.assertTrue(result.isSuccess());

        var soul = result.getOrThrow();
        Assertions.assertNotNull(soul);

        // Ensure data matches
        Assertions.assertEquals(EntityType.ALLAY, soul.entityType());
        Assertions.assertEquals(10d, soul.entityTag().getDouble("Health"));

        // Ensure unwanted key is removed.
        Assertions.assertFalse(soul.entityTag().contains("id"));
    }

    // Pre 8.0.5 format
    private static final String OLD_FORMAT = """
        {
            "entityTag": {
                "id": "minecraft:allay",
                "Health": 10.0
            }
        }
        """;

    @Test
    public void testLoadOldFormat(MinecraftServer server) {
        // Parse json
        var json = JsonParser.parseString(OLD_FORMAT);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = Soul.CODEC.parse(ops, json);

        Assertions.assertTrue(result.isSuccess());

        var soul = result.getOrThrow();
        Assertions.assertNotNull(soul);

        // Ensure data matches
        Assertions.assertEquals(EntityType.ALLAY, soul.entityType());
        Assertions.assertEquals(10d, soul.entityTag().getDouble("Health"));
    }
}
