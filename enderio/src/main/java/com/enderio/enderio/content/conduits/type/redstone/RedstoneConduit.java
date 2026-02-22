package com.enderio.enderio.content.conduits.type.redstone;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.bundle.SlotType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOConduitTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.Optional;
import java.util.Set;

public record RedstoneConduit(ResourceLocation texture, ResourceLocation activeTexture, Component description, Optional<ResourceKey<CreativeModeTab>> creativeTab)
        implements Conduit<RedstoneConduit, RedstoneConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<RedstoneConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
        .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(RedstoneConduit::texture),
            ResourceLocation.CODEC.fieldOf("active_texture").forGetter(RedstoneConduit::activeTexture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(RedstoneConduit::description),
            ResourceKey.codec(Registries.CREATIVE_MODE_TAB).optionalFieldOf("creative_tab").forGetter(Conduit::creativeTab))
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
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(EIOTags.Blocks.REDSTONE_CONNECTABLE)
                || blockState.canRedstoneConnectTo(level, neighbor, direction.getOpposite());
    }

    @Override
    public boolean canForceConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return !blockState.isAir();
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
    public int getIndexForLegacySlot(SlotType slotType) {
        return switch (slotType) {
        case FILTER_EXTRACT -> EXTRACT_FILTER_SLOT;
        case FILTER_INSERT -> INSERT_FILTER_SLOT;
        default -> -1;
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
    public int compareTo(@NotNull RedstoneConduit o) {
        return 0;
    }
}
