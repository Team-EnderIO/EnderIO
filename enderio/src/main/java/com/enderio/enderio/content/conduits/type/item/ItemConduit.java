package com.enderio.enderio.content.conduits.type.item;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathPropertyConsumer;
import com.enderio.enderio.api.conduits.connection.path.SpeedAndTickRatePair;
import com.enderio.enderio.config.conduits.ConduitsConfig;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.init.EIOConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jspecify.annotations.Nullable;
import org.joml.Vector2i;

import java.util.function.Consumer;

public record ItemConduit(Identifier texture, Component description, int transferRatePerCycle, int networkTickRate)
    implements Conduit<ItemConduit, ItemConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<ItemConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
        .group(Identifier.CODEC.fieldOf("texture").forGetter(Conduit::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description),
            // Using optionals in order to support the old conduit format.
            Codec.INT.optionalFieldOf("transfer_rate", 4).forGetter(ItemConduit::transferRatePerCycle),
            Codec.intRange(1, 20).optionalFieldOf("ticks_per_cycle", 20).forGetter(ItemConduit::networkTickRate))
        .apply(builder, ItemConduit::new));

    public static final ConnectionPathProperty<SpeedAndTickRatePair> PATH_SPEED_AND_TICK_RATE = SpeedAndTickRatePair.minProperty(SpeedAndTickRatePair.ZERO);

    @Override
    public ConduitType<ItemConduit, ItemConduitConnectionConfig> type() {
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
    public boolean canConnectToConduit(ItemConduit other) {
        return ConduitsConfig.COMMON.CAN_MIX_ITEM_CONDUIT_TIERS.get();
    }

    @Override
    public void collectNodePathProperties(ConduitNode node, ConnectionPathPropertyConsumer consumer) {
        consumer.accept(PATH_SPEED_AND_TICK_RATE, new SpeedAndTickRatePair(transferRatePerCycle(), networkTickRate()));
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag,
        DataComponentGetter dataComponentGetter) {
        String calculatedTransferLimitFormatted = String.format("%,d",
            (int) Math.floor(transferRatePerCycle() * (20.0 / networkTickRate())));
        consumer.accept(
            TooltipUtil.styledWithArgs(ConduitLang.ITEM_EFFECTIVE_RATE_TOOLTIP, calculatedTransferLimitFormatted));

        if (tooltipFlag.hasShiftDown()) {
            String transferLimitFormatted = String.format("%,d", transferRatePerCycle());
            consumer.accept(TooltipUtil.styledWithArgs(ConduitLang.ITEM_RAW_RATE_TOOLTIP, transferLimitFormatted));
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
    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        var capability = capabilityAccessor.getSidedCapability(Capabilities.Item.BLOCK, direction);
        return capability != null;
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
    public Component getSlotTooltip(int slot) {
        return switch (slot) {
            case EXTRACT_FILTER_SLOT -> ConduitLang.ITEM_FILTER_SLOT_TOOLTIP;
            case INSERT_FILTER_SLOT -> ConduitLang.ITEM_FILTER_SLOT_TOOLTIP;
            default -> Component.empty();
        };
    }

    @Override
    @Nullable
    public CompoundTag getExtraGuiData(ConduitBundle conduitBundle, ConduitNode node, Direction side) {
        if (!node.isConnectedToBlock(side)) {
            return null;
        }

        var config = node.getConnectionConfig(side, type().connectionConfigType());
        if (!config.extractRedstoneControl().isRedstoneSensitive()) {
            return null;
        }

        CompoundTag tag = new CompoundTag();
        tag.putBoolean("HasRedstoneSignal", node.hasRedstoneSignal(config.extractRedstoneChannel()));
        tag.putBoolean("HasRedstoneConduit", conduitBundle.hasConduitOfType(EIOConduitTypes.REDSTONE.get()));
        return tag;
    }

    @Override
    public int compareTo(ItemConduit o) {
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
