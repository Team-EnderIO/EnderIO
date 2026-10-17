package com.enderio.enderio.content.conduits.type.fluid;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathPropertyConsumer;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NonNull;
import org.joml.Vector2i;

import java.util.function.Consumer;

public record FluidConduit(Identifier texture, Component description, int transferRatePerTick)
    implements Conduit<FluidConduit, FluidConduitConnectionConfig> {

    public static final SingleResourceSlotKey<ItemResource> EXTRACT_FILTER_SLOT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> INSERT_FILTER_SLOT = new SingleResourceSlotKey<>();

    public static final ItemStorageLayout INVENTORY_LAYOUT = ItemStorageLayout.builder()
        .add(EXTRACT_FILTER_SLOT, builder -> builder
            .filter((_, resource) -> resource.toStack().getCapability(EnderIOCapabilities.FLUID_FILTER) != null))
        .add(INSERT_FILTER_SLOT, builder -> builder
            .filter((_, resource) -> resource.toStack().getCapability(EnderIOCapabilities.FLUID_FILTER) != null))
        .build();

    public static final MapCodec<FluidConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
        .group(Identifier.CODEC.fieldOf("texture").forGetter(Conduit::texture),
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
    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        var capability = capabilityAccessor.getSidedCapability(Capabilities.Fluid.BLOCK, direction);
        return capability != null;
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
            return ConduitLang.FLUID_FILTER_SLOT_TOOLTIP;
        }

        if (slot == INVENTORY_LAYOUT.indexOf(INSERT_FILTER_SLOT)) {
            return ConduitLang.FLUID_FILTER_SLOT_TOOLTIP;
        }

        throw new IndexOutOfBoundsException();
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag,
        DataComponentGetter dataComponentGetter) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        consumer
            .accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_EFFECTIVE_RATE_TOOLTIP, transferLimitFormatted));

        if (tooltipFlag.hasShiftDown()) {
            String rawRateFormatted = String.format("%,d",
                    (int) Math.ceil(transferRatePerTick() * type().getTickRate(this)));
            consumer.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RAW_RATE_TOOLTIP, rawRateFormatted));
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
    public int compareTo(@NonNull FluidConduit o) {
        if (transferRatePerTick() < o.transferRatePerTick()) {
            return -1;
        } else if (transferRatePerTick() > o.transferRatePerTick()) {
            return 1;
        }

        return 0;
    }
}
