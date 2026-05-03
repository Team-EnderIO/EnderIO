package com.enderio.enderio.tests.conduits.regression;

import com.enderio.enderio.content.conduits.legacy.DynamicConnectionState;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * This tests that when an item is removed from the game, legacy V7 conduit data can still be parsed.
 */
@SuppressWarnings("deprecation")
@ExtendWith(EphemeralTestServerProvider.class)
public class UpgradeToV8RemovedUpgradeItemsCausingLoadFailureTests {
    private static final String EXAMPLE_JSON = """
        {
            "is_insert": true,
            "insert_channel": "green",
            "is_extract": true,
            "extract_channel": "red",
            "redstone_control": "never_active",
            "redstone_channel": "red",
            "filter_insert": "minecraft:this_item_is_not_real",
            "filter_extract": "minecraft:this_item_is_not_real",
            "upgrade_extract": "minecraft:this_item_is_not_real"
        }
        """;

    @Test
    public void testDeserializeV7RS2NodeData(MinecraftServer server) {
        // Parse json
        var json = JsonParser.parseString(EXAMPLE_JSON);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = DynamicConnectionState.CODEC.parse(ops, json);

        Assertions.assertTrue(result.isSuccess());
    }
}
