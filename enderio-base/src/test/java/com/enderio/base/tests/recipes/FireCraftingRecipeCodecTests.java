package com.enderio.base.tests.recipes;

import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

@ExtendWith(EphemeralTestServerProvider.class)
public class FireCraftingRecipeCodecTests {
    @Test
    public void testDeserializeWithOptionals(MinecraftServer server) {
        var recipe = checkAndParseRecipe(server, """
            {
                "results": [
                    {
                        "result": {
                            "id": "minecraft:coal"
                        },
                        "min_count": 1,
                        "max_count": 2,
                        "chance": 0.5
                    }
                ],
                "dimensions": [
                    "minecraft:overworld"
                ]
            }
            """);

        Assertions.assertAll(
            () -> Assertions.assertEquals(1, recipe.results().size()),
            () -> Assertions.assertEquals(Items.COAL, recipe.results().getFirst().result().getItem()),
            () -> Assertions.assertEquals(1, recipe.results().getFirst().result().getCount()),
            () -> Assertions.assertEquals(1, recipe.results().getFirst().minCount()),
            () -> Assertions.assertEquals(2, recipe.results().getFirst().maxCount()),
            () -> Assertions.assertEquals(0.5, recipe.results().getFirst().chance(), 0.00001),
            () -> Assertions.assertEquals(1, recipe.dimensions().size()),
            () -> Assertions.assertEquals(Level.OVERWORLD, recipe.dimensions().getFirst()),
            () -> Assertions.assertEquals(0, recipe.bases().size()),
            () -> Assertions.assertEquals(0, recipe.baseTags().size()),
            () -> Assertions.assertEquals(Optional.empty(), recipe.blockAfterBurning())
        );
    }

    @Test
    public void testDeserializeWithAllFields(MinecraftServer server) {
        var recipe = checkAndParseRecipe(server, """
            {
                "results": [
                    {
                        "result": {
                            "id": "minecraft:coal",
                            "count": 10
                        },
                        "min_count": 1,
                        "max_count": 2,
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

        Assertions.assertAll(
            () -> Assertions.assertEquals(1, recipe.results().size()),
            () -> Assertions.assertEquals(Items.COAL, recipe.results().getFirst().result().getItem()),
            () -> Assertions.assertEquals(10, recipe.results().getFirst().result().getCount()),
            () -> Assertions.assertEquals(1, recipe.results().getFirst().minCount()),
            () -> Assertions.assertEquals(2, recipe.results().getFirst().maxCount()),
            () -> Assertions.assertEquals(0.5, recipe.results().getFirst().chance(), 0.00001),
            () -> Assertions.assertEquals(1, recipe.dimensions().size()),
            () -> Assertions.assertEquals(Level.OVERWORLD, recipe.dimensions().getFirst()),
            () -> Assertions.assertEquals(1, recipe.bases().size()),
            () -> Assertions.assertEquals(Blocks.STONE, recipe.bases().getFirst()),
            () -> Assertions.assertEquals(1, recipe.baseTags().size()),
            () -> Assertions.assertEquals(BlockTags.ACACIA_LOGS, recipe.baseTags().getFirst()),
            () -> Assertions.assertEquals(Optional.of(Blocks.COAL_BLOCK), recipe.blockAfterBurning())
        );
    }

    private FireCraftingRecipe checkAndParseRecipe(MinecraftServer server, String jsonString) {
        // Parse json
        var json = JsonParser.parseString(jsonString);

        // Attempt to deserialize
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = FireCraftingRecipe.Serializer.CODEC.codec().parse(ops, json);
        Assertions.assertTrue(result.isSuccess());
        return result.getOrThrow();
    }
}
