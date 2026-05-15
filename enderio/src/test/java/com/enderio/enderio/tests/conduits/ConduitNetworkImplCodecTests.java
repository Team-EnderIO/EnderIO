package com.enderio.enderio.tests.conduits;

import com.enderio.enderio.content.conduits.network.ConduitNetworkImpl;
import com.enderio.enderio.content.conduits.network.ConduitNodeImpl;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class ConduitNetworkImplCodecTests {

    private static final String OLD_NETWORK = """
        {
          "conduit" : "enderio:item",
          "nodes" : [ {
            "pos" : [ 0, 0, 0 ]
          } ],
          "edges" : [ ]
        }""";

    private static final String NEW_NETWORK = """
        {
          "conduit_type" : "enderio:item",
          "nodes" : [ {
            "pos" : [ 0, 0, 0 ],
            "conduit" : "enderio:item"
          } ],
          "edges" : [ ]
        }""";

    @Test
    public void loadLegacyForJsonCodec(MinecraftServer server) {

        var json = JsonParser.parseString(OLD_NETWORK);

        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = ConduitNetworkImpl.LEGACY_CODEC.decode(ops, json);

        var network = result.getOrThrow().getFirst();

        var data = ConduitNetworkImpl.NEW_CODEC.encodeStart(ops, network).result().get();

        result = ConduitNetworkImpl.CODEC.decode(ops, data);

        Pair<ConduitNetworkImpl, JsonElement> pair = result.getOrThrow();
        var newNetwork = pair.getFirst();

        Assertions.assertEquals(network.conduits(), newNetwork.conduits());

        ConduitNodeImpl node = network.nodes().stream().findFirst().get();
        ConduitNodeImpl newNode = newNetwork.nodes().stream().findFirst().get();
        Assertions.assertEquals(node.pos(), newNode.pos());
        Assertions.assertEquals(node.conduit(), newNode.conduit());

        Assertions.assertEquals(JsonParser.parseString(NEW_NETWORK), pair.getSecond());
    }
}
