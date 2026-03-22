package com.enderio.enderio.content.conduits.type.fluid;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.joml.Vector2i;

import java.util.function.Consumer;

public record FluidConduit(Identifier texture, Component description, int transferRatePerTick)
    implements Conduit<FluidConduit, FluidConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

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
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        var capability = level.getCapability(Capabilities.Fluid.BLOCK, conduitPos.relative(direction),
                direction.getOpposite());
        return capability != null;
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
