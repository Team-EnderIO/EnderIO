package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.ConduitBlockConnection;
import com.enderio.conduits.api.network.node.IConduitNode;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Comparator;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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

public record EnergyConduit(ResourceLocation texture, Component description, int transferRatePerTick)
        implements Conduit<EnergyConduit, EnergyConduitConnectionConfig> {

    public static final MapCodec<EnergyConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Conduit::texture),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description),
                    Codec.INT.fieldOf("transfer_rate").forGetter(EnergyConduit::transferRatePerTick))
            .apply(builder, EnergyConduit::of));

    public static EnergyConduit of(ResourceLocation texture, Component description, int transferRate) {
        return new EnergyConduit(texture, description, transferRate);
    }

    // Not configurable - energy is instantaneous
    @Override
    public int networkTickRate() {
        return 1;
    }

    @Override
    public ConduitType<EnergyConduit> type() {
        return ConduitTypes.ENERGY.get();
    }

    @Override
    public EnergyConduitTicker ticker() {
        return EnergyConduitTicker.INSTANCE;
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canBeInSameBundle(Holder<Conduit<?, ?>> otherConduit) {
        return !(otherConduit.value() instanceof EnergyConduit);
    }

    @Override
    public boolean canBeReplacedBy(Holder<Conduit<?, ?>> otherConduit) {
        if (!(otherConduit.value() instanceof EnergyConduit otherEnergyConduit)) {
            return false;
        }

        return compareTo(otherEnergyConduit) < 0;
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                conduitPos.relative(direction), direction.getOpposite());
        return capability != null;
    }

    @Override
    public Comparator<ConduitBlockConnection> getGeneralConnectionComparator() {
        return (a, b) -> Integer.compare(
            b.connectionConfig(EnergyConduitConnectionConfig.TYPE).priority(),
            a.connectionConfig(EnergyConduitConnectionConfig.TYPE).priority());
    }

    @Override
    public <TCap, TContext> @Nullable TCap proxyCapability(Level level, @Nullable IConduitNode node,
            BlockCapability<TCap, TContext> capability, @Nullable TContext context) {

        if (Capabilities.EnergyStorage.BLOCK == capability && (context == null || context instanceof Direction)) {
            boolean isMutable = true;

            if (node != null && context != null) {
                Direction side = (Direction) context;

                // No connection, no cap.
                if (!node.isConnectedToBlock(side)) {
                    return null;
                }

                var config = node.getConnectionConfig(side, connectionConfigType());
                if (!config.isConnected() || !config.isExtract()) {
                    return null;
                }

                if (config.extractRedstoneControl() == RedstoneControl.NEVER_ACTIVE) {
                    isMutable = false;
                } else if (config.extractRedstoneControl() != RedstoneControl.ALWAYS_ACTIVE) {
                    boolean hasRedstone = node.hasRedstoneSignal(config.extractRedstoneChannel());
                    if (!hasRedstone) {
                        for (Direction direction : Direction.values()) {
                            if (level.getSignal(node.pos().relative(direction), direction.getOpposite()) > 0) {
                                hasRedstone = true;
                                break;
                            }
                        }
                    }

                    if (!hasRedstone) {
                        isMutable = false;
                    }
                }
            }

            // noinspection unchecked
            return (TCap) new EnergyConduitStorage(isMutable, transferRatePerTick(), node);
        }

        return null;
    }

    @Override
    public void onRemoved(IConduitNode node, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public ConnectionConfigType<EnergyConduitConnectionConfig> connectionConfigType() {
        return ConduitTypes.ConnectionTypes.ENERGY.get();
    }

    @Override
    public EnergyConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, redstoneControl, redstoneChannel, 0);
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder,
            TooltipFlag pTooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.ENERGY_RATE_TOOLTIP, transferLimitFormatted));
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
