package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record EnergyConduit(
    ResourceLocation texture,
    Component description,
    int transferRate
) implements Conduit<EnergyConduit> {

    public static final MapCodec<EnergyConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder
            .group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Conduit::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description),
                Codec.INT.fieldOf("transfer_rate").forGetter(EnergyConduit::transferRate)
            ).apply(builder, EnergyConduit::of)
    );

    public static EnergyConduit of (ResourceLocation texture, Component description, int transferRate) {
        return new EnergyConduit(texture, description, transferRate);
    }

    private static final EnergyConduitTicker TICKER = new EnergyConduitTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    @Override
    public ConduitType<EnergyConduit> type() {
        return ConduitTypes.ENERGY.get();
    }

    @Override
    public EnergyConduitTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public boolean canBeInSameBundle(Holder<Conduit<?>> otherConduit) {
        return !(otherConduit.value() instanceof EnergyConduit);
    }

    @Override
    public boolean canBeReplacedBy(Holder<Conduit<?>> otherConduit) {
        if (!(otherConduit.value() instanceof EnergyConduit otherEnergyConduit)) {
            return false;
        }

        return compareTo(otherEnergyConduit) > 0;
    }

    @Override
    public <K> @Nullable K proxyCapability(BlockCapability<K, Direction> capability, ConduitNode node,
        Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {

        if (Capabilities.EnergyStorage.BLOCK == capability && (state == null || state.isExtract())) {
            //noinspection unchecked
            return (K)new EnergyConduitStorage(transferRate(), node);
        }

        return null;
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
        if (capability != null) {
            // Always default to both directions.
            return new ConduitConnectionData(true, true, RedstoneControl.ALWAYS_ACTIVE);
        }

        return Conduit.super.getDefaultConnection(level, pos, direction);
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRate());
        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.ENERGY_RATE_TOOLTIP, transferLimitFormatted));
    }

    @Override
    public int compareTo(@NotNull EnergyConduit o) {
        if (transferRate() < o.transferRate()) {
            return -1;
        } else if (transferRate() > o.transferRate()) {
            return 1;
        }

        return 0;
    }
}
