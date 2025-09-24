package com.enderio.base.tests.recipes;

import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class FireCraftingRecipeCodecTests {
    @Test
    public void testDeserializeWithOptionals(MinecraftServer server) {
        assertDeserializationIsSuccessful(server, """
            {
                "results": [
                    {
                        "result": {
                            "id": "minecraft:coal"
                        },
                        "min_count": 1,
                        "max_count": 1,
                        "chance": 0.5
                    }
                ],
                "dimensions": [
                    "minecraft:overworld"
                ]
            }
            """);
    }

    @Test
    public void testDeserializeWithAllFields(MinecraftServer server) {
        assertDeserializationIsSuccessful(server, """
            {
                "results": [
                    {
                        "result": {
                            "id": "minecraft:coal",
                            "count": 10
                        },
                        "min_count": 1,
                        "max_count": 1,
                        "chance": 0.5
                    }
                ],
                "dimensions": [
                    "minecraft:overworld"
                ],
                "base_blocks": [
                    "minecraft:stone"
                ],
                "base_tags": [
                    "minecraft:acacia_logs"
                ],
                "block_after_burning": "minecraft:coal_block"
            }
            """);
    }

    private void assertDeserializationIsSuccessful(MinecraftServer server, String jsonString) {
        // Parse json
        var json = JsonParser.parseString(jsonString);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = FireCraftingRecipe.Serializer.CODEC.codec().parse(ops, json);
        Assertions.assertTrue(result.isSuccess());
    }
}
