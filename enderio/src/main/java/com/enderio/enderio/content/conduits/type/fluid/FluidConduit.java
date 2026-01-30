package com.enderio.enderio.content.conduits.type.fluid;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.bundle.SlotType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.network.node.legacy.ConduitDataAccessor;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.init.EIOConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record FluidConduit(ResourceLocation texture, Component description, int transferRatePerTick,
        boolean isMultiFluid, boolean doesSupportPriority) implements Conduit<FluidConduit, FluidConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<FluidConduit> CODEC = RecordCodecBuilder
            .mapCodec(
                    builder -> builder
                            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(FluidConduit::texture),
                                    ComponentSerialization.CODEC.fieldOf("description")
                                            .forGetter(FluidConduit::description),
                                    Codec.INT.fieldOf("transfer_rate").forGetter(FluidConduit::transferRatePerTick),
                                    Codec.BOOL.fieldOf("is_multi_fluid").forGetter(FluidConduit::isMultiFluid),
                                Codec.BOOL.optionalFieldOf("does_support_priority", false).forGetter(FluidConduit::doesSupportPriority))
                            .apply(builder, FluidConduit::new));

    @Override
    public ConduitType<FluidConduit> type() {
        return EIOConduitTypes.FLUID.get();
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public int compareNodes(ConduitBlockConnection refConnection, ConduitBlockConnection connectionA, ConduitBlockConnection connectionB) {
        if (doesSupportPriority()) {
            int priorityA = connectionA.connectionConfig(FluidConduitConnectionConfig.TYPE).insertPriority();
            int priorityB = connectionB.connectionConfig(FluidConduitConnectionConfig.TYPE).insertPriority();
            if (priorityA != priorityB) {
                return Integer.compare(priorityB, priorityA);
            }
        }

        return Conduit.super.compareNodes(refConnection, connectionA, connectionB);
    }

    @Override
    public boolean canReplaceConduit(FluidConduit otherConduit) {
        return compareTo(otherConduit) > 0;
    }

    @Override
    public boolean hasServerConnectionChecks() {
        return !isMultiFluid();
    }

    @Override
    public boolean canConnectConduits(ConduitNode selfNode, ConduitNode otherNode) {
        if (isMultiFluid()) {
            return true;
        }

        // Ensure the networks are not locked to different fluids before connecting.
        var selfNetwork = selfNode.getNetwork();
        var otherNetwork = otherNode.getNetwork();

        // If one network does not yet exist, then we're good to connect.
        if (selfNetwork == null || otherNetwork == null) {
            return true;
        }

        var selfContext = selfNetwork.getContext(FluidConduitNetworkContext.TYPE);
        var otherContext = otherNetwork.getContext(FluidConduitNetworkContext.TYPE);

        if (selfContext == null || otherContext == null) {
            return true;
        }

        if (selfContext.lockedFluid().isSame(Fluids.EMPTY) || otherContext.lockedFluid().isSame(Fluids.EMPTY)) {
            return true;
        }

        return selfContext.lockedFluid().isSame(otherContext.lockedFluid());
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        IFluidHandler capability = level.getCapability(Capabilities.FluidHandler.BLOCK, conduitPos.relative(direction),
                direction.getOpposite());
        return capability != null;
    }

    @Override
    public ConnectionConfigType<FluidConduitConnectionConfig> connectionConfigType() {
        return FluidConduitConnectionConfig.TYPE;
    }

    @Override
    public FluidConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new FluidConduitConnectionConfig(isInsert, inputChannel, isExtract, outputChannel, redstoneControl,
                redstoneChannel, 0);
    }

    @Override
    public void copyLegacyData(ConduitNode node, ConduitDataAccessor legacyDataAccessor,
            BiConsumer<Direction, ConnectionConfig> connectionConfigSetter) {
        var legacyData = legacyDataAccessor.getData(EIOConduitTypes.Data.FLUID.get());
        if (legacyData == null) {
            return;
        }

        var context = Objects.requireNonNull(node.getNetwork()).getOrCreateContext(FluidConduitNetworkContext.TYPE);

        if (!context.lockedFluid().isSame(Fluids.EMPTY)) {
            return;
        }

        // Copy locked fluid from old data.
        context.setLockedFluid(legacyData.lockedFluid());
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getCapability(EnderIOCapabilities.FLUID_FILTER) != null;
    }

    @Override
    public Vector2i getInventorySlotPosition(int slot) {
        return switch (slot) {
        case EXTRACT_FILTER_SLOT -> new Vector2i(113, 71);
        case INSERT_FILTER_SLOT -> new Vector2i(23, 71);
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
    public @Nullable CompoundTag getExtraGuiData(ConduitBundle conduitBundle, ConduitNode node, Direction side) {
        return getExtraWorldData(conduitBundle, node);
    }

    @Override
    @Nullable
    public CompoundTag getExtraWorldData(ConduitBundle conduitBundle, ConduitNode node) {
        if (node.getNetwork() == null) {
            return null;
        }

        var context = node.getNetwork().getContext(FluidConduitNetworkContext.TYPE);
        if (context == null) {
            return null;
        }

        if (context.lockedFluid().isSame(Fluids.EMPTY)) {
            return null;
        }

        var tag = new CompoundTag();
        tag.putString("LockedFluid", BuiltInRegistries.FLUID.getKey(context.lockedFluid()).toString());
        return tag;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder,
            TooltipFlag tooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        tooltipAdder
                .accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_EFFECTIVE_RATE_TOOLTIP, transferLimitFormatted));

        if (isMultiFluid()) {
            tooltipAdder.accept(ConduitLang.MULTI_FLUID_TOOLTIP);
        }

        if (tooltipFlag.hasShiftDown()) {
            String rawRateFormatted = String.format("%,d",
                    (int) Math.ceil(transferRatePerTick() * (20.0 / type().ticker().tickRate())));
            tooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RAW_RATE_TOOLTIP, rawRateFormatted));
        }
    }

    @Override
    public boolean hasAdvancedTooltip() {
        return true;
    }

    @Override
    public boolean showDebugTooltip() {
        return true;
    }

    @Override
    public int compareTo(@NotNull FluidConduit o) {
        if (isMultiFluid() && !o.isMultiFluid()) {
            return 1;
        }

        if (transferRatePerTick() < o.transferRatePerTick()) {
            return -1;
        } else if (transferRatePerTick() > o.transferRatePerTick()) {
            return 1;
        }

        return 0;
    }
}
