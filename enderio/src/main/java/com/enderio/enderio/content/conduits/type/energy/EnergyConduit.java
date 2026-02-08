package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathPropertyConsumer;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.init.EIOConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record EnergyConduit(ResourceLocation texture, Component description, int transferRatePerTick)
        implements Conduit<EnergyConduit, EnergyConduitConnectionConfig> {

    public static final MapCodec<EnergyConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Conduit::texture),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description),
                    Codec.INT.fieldOf("transfer_rate").forGetter(EnergyConduit::transferRatePerTick))
            .apply(builder, EnergyConduit::new));
    
    @Override
    public ConduitType<EnergyConduit, EnergyConduitConnectionConfig> type() {
        return EIOConduitTypes.ENERGY.get();
    }

    // Not configurable - energy is instantaneous
    @Override
    public int networkTickRate() {
        return 1;
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canReplaceConduit(EnergyConduit otherConduit) {
        return compareTo(otherConduit) > 0;
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                conduitPos.relative(direction), direction.getOpposite());
        return capability != null;
    }

    @Override
    public <TCap, TContext> @Nullable TCap proxyCapability(Level level, @Nullable ConduitNode node,
            BlockCapability<TCap, TContext> capability, @Nullable TContext context) {

        if (Capabilities.EnergyStorage.BLOCK == capability && (context == null || context instanceof Direction)) {
            Direction side = (Direction) context;

            if (node != null && context != null) {
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
            return (TCap) new EnergyConduitStorage(side, transferRatePerTick(), node);
        }

        return null;
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public EnergyConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, redstoneControl, redstoneChannel, 0);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder,
            TooltipFlag tooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        tooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.ENERGY_RATE_TOOLTIP, transferLimitFormatted));
    }

    @Override
    public int compareTo(@NotNull EnergyConduit o) {
        if (transferRatePerTick() < o.transferRatePerTick()) {
            return -1;
        } else if (transferRatePerTick() > o.transferRatePerTick()) {
            return 1;
        }

        return 0;
    }
}
