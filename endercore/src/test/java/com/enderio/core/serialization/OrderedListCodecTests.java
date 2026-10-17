package com.enderio.core.serialization;

import com.enderio.core.common.serialization.OrderedListCodec;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

// The main value in these tests is ensuring that unintended changes aren't made to the codec which would result in save corruption.
@ExtendWith(EphemeralTestServerProvider.class)
public class OrderedListCodecTests {
    private static final Codec<List<String>> CODEC = OrderedListCodec.create(Codec.STRING, "");
    private static final List<String> LIST = List.of("I am first", "I am second", "I am third");
    private static final JsonElement JSON_ENCODED = JsonParser.parseString(
        "[{\"index\":0,\"value\":\"I am first\"},{\"index\":1,\"value\":\"I am second\"},{\"index\":2,\"value\":\"I am third\"}]");

    @Test
    public void orderedListCodecSerialize(MinecraftServer server) {
        // Act.
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        JsonElement encoded = CODEC.encodeStart(ops, LIST).getOrThrow();

        // Assert.
        Assertions.assertEquals(JSON_ENCODED, encoded);
    }

    @Test
    public void orderedListCodecDeserialize(MinecraftServer server) {
        // Act.
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        List<String> deserialized = CODEC.decode(ops, JSON_ENCODED).getOrThrow().getFirst();

        // Assert.
        Assertions.assertEquals(LIST, deserialized);
    }
}
