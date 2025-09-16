package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitCapabilities;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.bundle.ConduitBundle;
import com.enderio.conduits.api.bundle.SlotType;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.node.IConduitNode;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.conduits.common.tag.ConduitTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public record RedstoneConduit(ResourceLocation texture, ResourceLocation activeTexture, Component description)
        implements Conduit<RedstoneConduit, RedstoneConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<RedstoneConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(RedstoneConduit::texture),
                    ResourceLocation.CODEC.fieldOf("active_texture").forGetter(RedstoneConduit::activeTexture),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(RedstoneConduit::description))
            .apply(builder, RedstoneConduit::new));

    @Override
    public int networkTickRate() {
        return 2;
    }

    @Override
    public ConduitType<RedstoneConduit> type() {
        return ConduitTypes.REDSTONE.get();
    }

    @Override
    public RedstoneConduitTicker ticker() {
        return RedstoneConduitTicker.INSTANCE;
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public void onConnectionsUpdated(IConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        node.markDirty();
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(ConduitTags.Blocks.REDSTONE_CONNECTABLE)
                || blockState.canRedstoneConnectTo(level, neighbor, direction.getOpposite());
    }

    @Override
    public boolean canForceConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return !blockState.isAir();
    }

    @Override
    public ConnectionConfigType<RedstoneConduitConnectionConfig> connectionConfigType() {
        return RedstoneConduitConnectionConfig.TYPE;
    }

    @Override
    public RedstoneConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new RedstoneConduitConnectionConfig(isInsert, inputChannel, isExtract, outputChannel, false);
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == EXTRACT_FILTER_SLOT) {
            return stack.getCapability(ConduitCapabilities.REDSTONE_EXTRACT_FILTER) != null;
        } else if (slot == INSERT_FILTER_SLOT) {
            return stack.getCapability(ConduitCapabilities.REDSTONE_INSERT_FILTER) != null;
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
    public int getIndexForLegacySlot(SlotType slotType) {
        return switch (slotType) {
        case FILTER_EXTRACT -> EXTRACT_FILTER_SLOT;
        case FILTER_INSERT -> INSERT_FILTER_SLOT;
        default -> -1;
        };
    }

    @Override
    public CompoundTag getExtraWorldData(ConduitBundle conduitBundle, IConduitNode node) {
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
    public int compareTo(@NotNull RedstoneConduit o) {
        return 0;
    }
}
