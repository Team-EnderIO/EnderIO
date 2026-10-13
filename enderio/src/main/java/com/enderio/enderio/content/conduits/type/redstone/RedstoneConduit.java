package com.enderio.enderio.content.conduits.type.redstone;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOConduitTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.joml.Vector2i;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public record RedstoneConduit(Identifier texture, Identifier activeTexture, Component description)
        implements Conduit<RedstoneConduit, RedstoneConduitConnectionConfig> {

    public static final SingleResourceSlotKey<ItemResource> EXTRACT_FILTER_SLOT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> INSERT_FILTER_SLOT = new SingleResourceSlotKey<>();

    public static final ItemStorageLayout INVENTORY_LAYOUT = ItemStorageLayout.builder()
        .add(EXTRACT_FILTER_SLOT, builder -> builder
            .filter((_, resource) -> resource.toStack().getCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER) != null))
        .add(INSERT_FILTER_SLOT, builder -> builder
            .filter((_, resource) -> resource.toStack().getCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER) != null))
        .build();

    public static final MapCodec<RedstoneConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
        .group(Identifier.CODEC.fieldOf("texture").forGetter(RedstoneConduit::texture),
            Identifier.CODEC.fieldOf("active_texture").forGetter(RedstoneConduit::activeTexture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(RedstoneConduit::description))
        .apply(builder, RedstoneConduit::new));

    @Override
    public ConduitType<RedstoneConduit, RedstoneConduitConnectionConfig> type() {
        return EIOConduitTypes.REDSTONE.get();
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public void onConnectionsUpdated(ConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        node.markDirty();
    }

    @Override
    public boolean shouldCheckConnectionsOnNeighborChange() {
        return true;
    }

    @Override
    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(EIOTags.Blocks.REDSTONE_CONNECTABLE)
                || blockState.shouldRedstoneWireConnectTo(level, neighbor, direction.getOpposite());
    }

    @Override
    public boolean canForceConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return !blockState.isAir();
    }

    @Override
    public ResourceStorageLayout<ItemResource> inventoryLayout() {
        return INVENTORY_LAYOUT;
    }

    @Override
    public Vector2i getInventorySlotPosition(int slot) {
        if (slot == INVENTORY_LAYOUT.indexOf(EXTRACT_FILTER_SLOT)) {
            return new Vector2i(113, 71);
        }

        if (slot == INVENTORY_LAYOUT.indexOf(INSERT_FILTER_SLOT)) {
            return new Vector2i(23, 71);
        }

        throw new IndexOutOfBoundsException();
    }


    @Override
    public Component getSlotTooltip(int slot) {
        if (slot == INVENTORY_LAYOUT.indexOf(EXTRACT_FILTER_SLOT)) {
            return ConduitLang.REDSTONE_FILTER_SLOT_TOOLTIP;
        }

        if (slot == INVENTORY_LAYOUT.indexOf(INSERT_FILTER_SLOT)) {
            return ConduitLang.REDSTONE_FILTER_SLOT_TOOLTIP;
        }

        throw new IndexOutOfBoundsException();
    }

    @Override
    public CompoundTag getExtraWorldData(ConduitBundle conduitBundle, ConduitNode node) {
        var tag = new CompoundTag();

        if (node.getNetwork() == null) {
            return tag;
        }

        var context = node.getNetwork().getContext(RedstoneConduitNetworkContext.TYPE);
        if (context != null) {
            tag.putBoolean("IsActive", context.isActive());
        }

        return tag;
    }

    @Override
    public int compareTo(@NonNull RedstoneConduit o) {
        return 0;
    }
}
