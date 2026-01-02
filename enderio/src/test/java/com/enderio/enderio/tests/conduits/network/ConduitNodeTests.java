package com.enderio.enderio.tests.conduits.network;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.enderio.content.conduits.network.ConduitNodeImpl;
import com.enderio.enderio.content.conduits.type.item.ItemConduitConnectionConfig;
import com.enderio.enderio.content.conduits.type.item.ItemConduitNodeData;
import com.enderio.enderio.init.EIOConduits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class ConduitNodeTests {
    @Test
    public void detachedNodeIsLoaded(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);
        Assertions.assertFalse(conduitNode.isLoaded());
        Assertions.assertFalse(conduitNode.isTicking());
    }

    @Test
    public void testHasNodeData_TypedDataAvailable(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        // Set the same data before
        var dataBefore = ItemConduitNodeData.TYPE.create();
        dataBefore.setIndex(Direction.UP, 12);
        dataBefore.setIndex(Direction.SOUTH, 18);
        conduitNode.setNodeData(dataBefore);

        Assertions.assertNotNull(conduitNode.getNodeData());

        // Check that hasNodeData (typed) returns true.
        Assertions.assertTrue(conduitNode.hasNodeData(ItemConduitNodeData.TYPE));
    }

    @Test
    public void testHasNodeData_DifferentDataTypeStored(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        // Set the data before with a different type.
        conduitNode.setNodeData(TestConduitNodeData.TYPE.create());

        Assertions.assertNotNull(conduitNode.getNodeData());

        // Check that hasNodeData (typed) returns false.
        Assertions.assertFalse(conduitNode.hasNodeData(ItemConduitNodeData.TYPE));
    }

    @Test
    public void testGetNodeDataWithType_TypedDataAvailable(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        // Set the same data before
        var dataBefore = ItemConduitNodeData.TYPE.create();
        dataBefore.setIndex(Direction.UP, 12);
        dataBefore.setIndex(Direction.SOUTH, 18);
        conduitNode.setNodeData(dataBefore);

        Assertions.assertNotNull(conduitNode.getNodeData());

        // Ensure the data is identical
        var nodeData = conduitNode.getNodeData(ItemConduitNodeData.TYPE);
        Assertions.assertNotNull(nodeData);
        Assertions.assertEquals(ItemConduitNodeData.TYPE, nodeData.type());
        Assertions.assertEquals(dataBefore, nodeData);
    }

    @Test
    public void testGetNodeDataWithType_DifferentDataTypeStored(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        // Set the data before with a different type.
        conduitNode.setNodeData(TestConduitNodeData.TYPE.create());

        Assertions.assertNotNull(conduitNode.getNodeData());

        // Ensure null is returned
        var nodeData = conduitNode.getNodeData(ItemConduitNodeData.TYPE);
        Assertions.assertNull(nodeData);
    }

    @Test
    public void testGetOrCreateNodeData_NoDataBefore(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        Assertions.assertNull(conduitNode.getNodeData());

        // Ensure the default data is created and matches the requested type
        var nodeData = conduitNode.getOrCreateNodeData(ItemConduitNodeData.TYPE);
        Assertions.assertNotNull(nodeData);
        Assertions.assertEquals(ItemConduitNodeData.TYPE, nodeData.type());
        Assertions.assertEquals(ItemConduitNodeData.TYPE.create(), nodeData);
    }

    @Test
    public void testGetOrCreateNodeData_SameDataTypeBefore(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        // Set the same data before
        var dataBefore = ItemConduitNodeData.TYPE.create();
        dataBefore.setIndex(Direction.UP, 12);
        dataBefore.setIndex(Direction.SOUTH, 18);
        conduitNode.setNodeData(dataBefore);

        Assertions.assertNotNull(conduitNode.getNodeData());

        // Ensure the data is identical
        var nodeData = conduitNode.getOrCreateNodeData(ItemConduitNodeData.TYPE);
        Assertions.assertNotNull(nodeData);
        Assertions.assertEquals(ItemConduitNodeData.TYPE, nodeData.type());
        Assertions.assertEquals(dataBefore, nodeData);
    }

    @Test
    public void testGetOrCreateNodeData_DifferentTypeStored(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        // Set the data before with a different type.
        conduitNode.setNodeData(TestConduitNodeData.TYPE.create());

        Assertions.assertNotNull(conduitNode.getNodeData());

        // Ensure new data is returned with the right type
        var nodeData = conduitNode.getOrCreateNodeData(ItemConduitNodeData.TYPE);
        Assertions.assertNotNull(nodeData);
        Assertions.assertEquals(ItemConduitNodeData.TYPE, nodeData.type());
        Assertions.assertEquals(ItemConduitNodeData.TYPE.create(), nodeData);
    }

    @Test
    public void ensureMethodsWhichRequireAttachmentThrow(MinecraftServer server) {
        var conduitsRegistry = server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var itemConduit = conduitsRegistry.getOrThrow(EIOConduits.ITEM);

        var conduitNode = new ConduitNodeImpl(itemConduit, BlockPos.ZERO);

        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.getNeighborSidedCapability(Capabilities.Energy.BLOCK, Direction.UP));
        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.getNeighborVoidCapability(EnderIOCapabilities.SOUL_BINDABLE_BLOCK, Direction.UP));
        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.hasRedstoneSignal(null));
        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.isConnectedToBlock(Direction.UP));
        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.isConnectedTo(Direction.UP));
        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.getConnectionConfig(Direction.UP));
        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.getConnectionConfig(Direction.UP, ItemConduitConnectionConfig.TYPE));
        Assertions.assertThrows(IllegalStateException.class, () -> conduitNode.getInventory(Direction.UP));
    }

    // TODO: Tests with the Fake node attachment.

    private static class TestConduitNodeData implements NodeData {

        public static NodeDataType<TestConduitNodeData> TYPE = new NodeDataType<>(null, TestConduitNodeData::new);

        @Override
        public NodeDataType<?> type() {
            return TYPE;
        }
    }
}
