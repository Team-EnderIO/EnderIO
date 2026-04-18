package com.enderio.enderio.content.conduits.type.redstone;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.joml.Vector2i;

import java.util.Set;

public record RedstoneConduit(Identifier texture, Identifier activeTexture, Component description)
        implements Conduit<RedstoneConduit, RedstoneConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

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
                || blockState.canRedstoneConnectTo(level, neighbor, direction.getOpposite());
    }

    @Override
    public boolean canForceConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return !blockState.isAir();
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == EXTRACT_FILTER_SLOT) {
            return stack.getCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER) != null;
        } else if (slot == INSERT_FILTER_SLOT) {
            return stack.getCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER) != null;
        }

        return false;
    }

    @Override
    public Vector2i getInventorySlotPosition(int slot) {
        return switch (slot) {
        case EXTRACT_FILTER_SLOT -> new Vector2i(23, 71);
        case INSERT_FILTER_SLOT -> new Vector2i(113, 71);
        default -> throw new IndexOutOfBoundsException();
        };
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
