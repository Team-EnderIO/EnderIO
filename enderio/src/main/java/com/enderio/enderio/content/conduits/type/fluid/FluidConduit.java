package com.enderio.enderio.content.conduits.type.fluid;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.bundle.SlotType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathPropertyConsumer;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.network.node.legacy.ConduitDataAccessor;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.config.conduits.ConduitsConfig;
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
import net.minecraft.world.level.BlockGetter;
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

public record FluidConduit(ResourceLocation texture, Component description, int transferRatePerTick)
    implements Conduit<FluidConduit, FluidConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<FluidConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
        .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Conduit::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description),
            Codec.INT.fieldOf("transfer_rate").forGetter(FluidConduit::transferRatePerTick))
        .apply(builder, FluidConduit::new));

    public static final ConnectionPathProperty<Integer> PATH_MAX_TRANSFER_RATE = ConnectionPathProperty.minInt(0);

    @Override
    public ConduitType<FluidConduit, FluidConduitConnectionConfig> type() {
        return EIOConduitTypes.FLUID.get();
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canReplaceConduit(FluidConduit otherConduit) {
        return compareTo(otherConduit) > 0;
    }

    @Override
    public boolean canConnectToConduit(FluidConduit other) {
        return ConduitsConfig.COMMON.CAN_MIX_FLUID_CONDUIT_TIERS.get();
    }

    @Override
    public void collectNodePathProperties(ConduitNode node, ConnectionPathPropertyConsumer consumer) {
        consumer.accept(PATH_MAX_TRANSFER_RATE, transferRatePerTick());
    }

    @Override
    public boolean shouldCheckConnectionsOnNeighborChange() {
        return false;
    }

    @Override
    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        IFluidHandler capability = capabilityAccessor.getSidedCapability(Capabilities.FluidHandler.BLOCK, direction);
        return capability != null;
    }

    @Override
    public FluidConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new FluidConduitConnectionConfig(isInsert, inputChannel, isExtract, outputChannel, redstoneControl,
                redstoneChannel, 0);
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
    public Component getSlotTooltip(int slot) {
        return switch (slot) {
            case EXTRACT_FILTER_SLOT -> ConduitLang.FLUID_FILTER_SLOT_TOOLTIP;
            case INSERT_FILTER_SLOT -> ConduitLang.FLUID_FILTER_SLOT_TOOLTIP;
            default -> Component.empty();
        };
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder,
            TooltipFlag tooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        tooltipAdder
                .accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_EFFECTIVE_RATE_TOOLTIP, transferLimitFormatted));

        if (tooltipFlag.hasShiftDown()) {
            String rawRateFormatted = String.format("%,d",
                    (int) Math.ceil(transferRatePerTick() * type().getTickRate(this)));
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
        if (transferRatePerTick() < o.transferRatePerTick()) {
            return -1;
        } else if (transferRatePerTick() > o.transferRatePerTick()) {
            return 1;
        }

        return 0;
    }
}
