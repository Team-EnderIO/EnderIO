package com.enderio.enderio.tests.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.content.conduits.network.ConduitNodeImpl;
import com.enderio.enderio.content.conduits.type.item.ItemConduitNodeData;
import com.enderio.enderio.init.EIOConduits;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Objects;

@ExtendWith(EphemeralTestServerProvider.class)
public class ConduitNodeImplCodecTests {

    @Test
    public void loadLegacyFormatWithNewCodec(MinecraftServer server) {
        var conduits = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = Objects.requireNonNull(conduits.get(EIOConduits.ITEM)).orElseThrow();

        var expectedNodeData = new ItemConduitNodeData(Map.of(Direction.NORTH, 5));
        var testNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO, expectedNodeData);

        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var result = ConduitNodeImpl.LEGACY_V8_0_CODEC.encodeStart(ops, testNode).result().orElseThrow();

        var resultNode = ConduitNodeImpl.CODEC.parse(ops, result).result().orElseThrow();

        // Expect the conduit to be null when loading old data, but it should load successfully and pos and node data should match.
        Assertions.assertNull(resultNode.conduit());
        Assertions.assertEquals(testNode.pos(), resultNode.pos());
        Assertions.assertEquals(testNode.getNodeData(), resultNode.getNodeData());
    }
}
