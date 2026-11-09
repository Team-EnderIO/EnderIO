package com.enderio.enderio.tests.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.conduits.type.item.ItemConduitConnectionConfig;
import com.enderio.enderio.content.conduits.type.item.ItemConduitNodeData;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOConduits;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class ConduitBundleLoadTests {
    private static Holder<Conduit<?, ?>> itemConduit(MinecraftServer server) {
        return server.registryAccess().lookupOrThrow(EnderIORegistries.Keys.CONDUIT)
            .getOrThrow(EIOConduits.ITEM);
    }

    private static CompoundTag savedBundle(MinecraftServer server, Holder<Conduit<?, ?>> conduit) {
        var ops = server.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        var tag = new CompoundTag();
        var conduits = new ListTag();
        conduits.add(Conduit.CODEC.encodeStart(ops, conduit).getOrThrow());
        tag.put("Conduits", conduits);

        var node = new CompoundTag();
        node.put("Conduit", Conduit.CODEC.encodeStart(ops, conduit).getOrThrow());
        var data = new ItemConduitNodeData();
        data.setIndex(Direction.NORTH, 7);
        node.put("Data", NodeData.GENERIC_CODEC
            .encodeStart(ops, data).getOrThrow());
        var nodes = new ListTag();
        nodes.add(node);
        tag.put("NodeData", nodes);

        var north = new CompoundTag();
        north.putString("Side", Direction.NORTH.getName());
        north.putString("Status", ConnectionStatus.CONNECTED_BLOCK.getSerializedName());
        north.put("Config", ConnectionConfig.GENERIC_CODEC.encodeStart(ops,
            ItemConduitConnectionConfig.DEFAULT).getOrThrow());
        var inventory = new ListTag();
        inventory.add(new ItemStack(EIOItems.BASIC_ITEM_FILTER.get()).save(server.registryAccess()));
        north.put("Inventory", inventory);
        var sides = new ListTag();
        sides.add(north);
        var connections = new ListTag();
        connections.add(sides);
        tag.put("Connections", connections);
        return tag;
    }

    @Test
    void nbtAppliedAfterSetLevelRestoresNodesBeforeConnectionUpdates(MinecraftServer server) {
        var conduit = itemConduit(server);
        var bundle = new ConduitBundleBlockEntity(new BlockPos(32, 100, 32),
            EIOBlocks.CONDUIT_BUNDLE.get().defaultBlockState());
        bundle.setLevel(server.overworld());
        bundle.loadWithComponents(savedBundle(server, conduit), server.registryAccess());

        var node = bundle.getConduitNode(conduit);
        Assertions.assertNotNull(node);
        Assertions.assertNotNull(node.getNetwork());
        Assertions.assertEquals(7, node.getNodeData(ItemConduitNodeData.TYPE).getIndex(Direction.NORTH));
        Assertions.assertEquals(ItemConduitConnectionConfig.DEFAULT,
            bundle.getConnectionConfig(conduit, Direction.NORTH));
        Assertions.assertEquals(ConnectionStatus.CONNECTED_BLOCK,
            bundle.getConnectionStatus(conduit, Direction.NORTH));
        var inventory = bundle.getConnectionInventory(conduit, Direction.NORTH);
        Assertions.assertNotNull(inventory);
        Assertions.assertTrue(inventory.getStackInSlot(0).is(EIOItems.BASIC_ITEM_FILTER.get()));
        Assertions.assertEquals(1, inventory.getStackInSlot(0).getCount());
        Assertions.assertDoesNotThrow(() -> bundle.setConnectionStatus(conduit, Direction.NORTH,
            ConnectionStatus.DISCONNECTED));
        Assertions.assertDoesNotThrow(() -> bundle.onConnectionsUpdated(conduit));
    }

    @Test
    void normalLoadOrderStillRestoresNodes(MinecraftServer server) {
        var conduit = itemConduit(server);
        var bundle = new ConduitBundleBlockEntity(new BlockPos(64, 100, 32),
            EIOBlocks.CONDUIT_BUNDLE.get().defaultBlockState());
        bundle.loadWithComponents(savedBundle(server, conduit), server.registryAccess());
        bundle.setLevel(server.overworld());

        Assertions.assertNotNull(bundle.getConduitNode(conduit));
        Assertions.assertEquals(7, bundle.getConduitNode(conduit)
            .getNodeData(ItemConduitNodeData.TYPE).getIndex(Direction.NORTH));
        Assertions.assertDoesNotThrow(() -> bundle.onConnectionsUpdated(conduit));
    }

    @Test
    void repeatedNbtLoadDoesNotReplaceAnAttachedNode(MinecraftServer server) {
        var conduit = itemConduit(server);
        var bundle = new ConduitBundleBlockEntity(new BlockPos(96, 100, 32),
            EIOBlocks.CONDUIT_BUNDLE.get().defaultBlockState());
        bundle.loadWithComponents(savedBundle(server, conduit), server.registryAccess());
        bundle.setLevel(server.overworld());
        var node = bundle.getConduitNode(conduit);
        var network = node.getNetwork();
        var saved = bundle.saveWithoutMetadata(server.registryAccess());

        bundle.loadWithComponents(saved, server.registryAccess());

        Assertions.assertSame(node, bundle.getConduitNode(conduit));
        Assertions.assertSame(network, node.getNetwork());
        Assertions.assertEquals(7, node.getNodeData(ItemConduitNodeData.TYPE).getIndex(Direction.NORTH));
    }
}
