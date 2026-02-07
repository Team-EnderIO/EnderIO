package com.enderio.enderio.content.conduits.type.item;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record ItemConduit(ResourceLocation texture, Component description, int transferRatePerCycle,
        int networkTickRate) implements Conduit<ItemConduit, ItemConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<ItemConduit> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(ItemConduit::texture),
                            ComponentSerialization.CODEC.fieldOf("description").forGetter(ItemConduit::description),
                            // Using optionals in order to support the old conduit format.
                            Codec.INT.optionalFieldOf("transfer_rate", 4).forGetter(ItemConduit::transferRatePerCycle),
                            Codec.intRange(1, 20)
                                    .optionalFieldOf("ticks_per_cycle", 20)
                                    .forGetter(ItemConduit::networkTickRate))
                    .apply(builder, ItemConduit::new));

    @Override
    public ConduitType<ItemConduit> type() {
        return EIOConduitTypes.ITEM.get();
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canReplaceConduit(ItemConduit otherConduit) {
        return compareTo(otherConduit) > 0;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder,
            TooltipFlag tooltipFlag) {
        String calculatedTransferLimitFormatted = String.format("%,d",
                (int) Math.floor(transferRatePerCycle() * (20.0 / networkTickRate())));
        tooltipAdder.accept(
                TooltipUtil.styledWithArgs(ConduitLang.ITEM_EFFECTIVE_RATE_TOOLTIP, calculatedTransferLimitFormatted));

        if (tooltipFlag.hasShiftDown()) {
            String transferLimitFormatted = String.format("%,d", transferRatePerCycle());
            tooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.ITEM_RAW_RATE_TOOLTIP, transferLimitFormatted));
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
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        IItemHandler capability = level.getCapability(Capabilities.ItemHandler.BLOCK, conduitPos.relative(direction),
                direction.getOpposite());
        return capability != null;
    }

    @Override
    public ConnectionConfigType<ItemConduitConnectionConfig> connectionConfigType() {
        return EIOConduitTypes.ConnectionTypes.ITEM.get();
    }

    @Override
    public ItemConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new ItemConduitConnectionConfig(isInsert, inputChannel, isExtract, outputChannel, redstoneControl,
                redstoneChannel, false, false, 0);
    }

    @Override
    public void copyLegacyData(ConduitNode node, ConduitDataAccessor legacyDataAccessor,
            BiConsumer<Direction, ConnectionConfig> connectionConfigSetter) {
        var legacyData = legacyDataAccessor.getData(EIOConduitTypes.Data.ITEM.get());
        if (legacyData == null) {
            return;
        }

        // Copy connection config
        for (Direction side : Direction.values()) {
            if (node.isConnectedToBlock(side)) {
                var oldSideConfig = legacyData.get(side);
                var currentConfig = node.getConnectionConfig(side, ItemConduitConnectionConfig.TYPE);

                connectionConfigSetter.accept(side,
                        currentConfig.withIsRoundRobin(oldSideConfig.isRoundRobin)
                                .withIsSelfFeed(oldSideConfig.isSelfFeed)
                                .withPriority(oldSideConfig.priority));
            }
        }
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getCapability(EnderIOCapabilities.ITEM_FILTER) != null;
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
    @Nullable
    public CompoundTag getExtraGuiData(ConduitBundle conduitBundle, ConduitNode node, Direction side) {
        if (!node.isConnectedToBlock(side)) {
            return null;
        }

        var config = node.getConnectionConfig(side, connectionConfigType());
        if (!config.extractRedstoneControl().isRedstoneSensitive()) {
            return null;
        }

        CompoundTag tag = new CompoundTag();
        tag.putBoolean("HasRedstoneSignal", node.hasRedstoneSignal(config.extractRedstoneChannel()));
        tag.putBoolean("HasRedstoneConduit", conduitBundle.hasConduitOfType(EIOConduitTypes.REDSTONE.get()));
        return tag;
    }

    @Override
    public int compareTo(@NotNull ItemConduit o) {
        double selfEffectiveSpeed = transferRatePerCycle() * (20.0 / networkTickRate());
        double otherEffectiveSpeed = o.transferRatePerCycle() * (20.0 / o.networkTickRate());

        if (selfEffectiveSpeed < otherEffectiveSpeed) {
            return -1;
        } else if (selfEffectiveSpeed > otherEffectiveSpeed) {
            return 1;
        }

        return 0;
    }
}
