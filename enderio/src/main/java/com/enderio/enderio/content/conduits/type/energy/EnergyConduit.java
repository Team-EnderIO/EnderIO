package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathPropertyConsumer;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import com.enderio.enderio.config.conduits.ConduitsConfig;
import java.util.function.Consumer;

public record EnergyConduit(Identifier texture, Component description, int transferRatePerTick)
        implements Conduit<EnergyConduit, EnergyConduitConnectionConfig> {

    public static final MapCodec<EnergyConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(Identifier.CODEC.fieldOf("texture").forGetter(Conduit::texture),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description),
                    Codec.INT.fieldOf("transfer_rate").forGetter(EnergyConduit::transferRatePerTick))
            .apply(builder, EnergyConduit::new));

    public static final ConnectionPathProperty<Integer> PATH_MAX_TRANSFER_RATE = ConnectionPathProperty.minInt(0);
    
    @Override
    public ConduitType<EnergyConduit, EnergyConduitConnectionConfig> type() {
        return EIOConduitTypes.ENERGY.get();
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canConnectToConduit(EnergyConduit other) {
        return ConduitsConfig.COMMON.CAN_MIX_ENERGY_CONDUIT_TIERS.get();
    }

    @Override
    public boolean canReplaceConduit(EnergyConduit otherConduit) {
        return compareTo(otherConduit) > 0;
    }

    @Override
    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        var capability = capabilityAccessor.getSidedCapability(Capabilities.Energy.BLOCK, direction);
        return capability != null;
    }

    @Override
    public void collectNodePathProperties(ConduitNode node, ConnectionPathPropertyConsumer consumer) {
        consumer.accept(PATH_MAX_TRANSFER_RATE, transferRatePerTick());
    }

    @Override
    public <TCap, TContext> @Nullable TCap proxyCapability(Level level, @Nullable ConduitNode node,
            BlockCapability<TCap, TContext> capability, @Nullable TContext context) {

        if (Capabilities.Energy.BLOCK == capability && context instanceof Direction side) {
            if (node != null) {
                // Disabled, do not offer the capability (so if we're disconnected we allow auto connect).
                // Note that this will introduce a minor quirk - if disabled, the cap will be invisible until re-enabled.
                // in the case of Energizer rods in Powah, you will not be able to place one against the disabled conduit.
                // This is necessary however, because many cables will retain a 'connected' appearance if the capability is still exposed.
                // See GH-1184 for the original bug report.
                // TODO: Review whether not hiding a disconnected cap could have other unforseen issues, such as cables attaching to conduits weirdly.
                if (node.getConnectionStatus(side) == ConnectionStatus.DISABLED) {
                    return null;
                }
            }

            // noinspection unchecked
            return (TCap) new EnergyConduitStorage(side, node);
        }

        return null;
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag,
        DataComponentGetter dataComponentGetter) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        consumer.accept(TooltipUtil.styledWithArgs(ConduitLang.ENERGY_RATE_TOOLTIP, transferLimitFormatted));
    }

    @Override
    public int compareTo(@NonNull EnergyConduit o) {
        if (transferRatePerTick() < o.transferRatePerTick()) {
            return -1;
        } else if (transferRatePerTick() > o.transferRatePerTick()) {
            return 1;
        }

        return 0;
    }
}
