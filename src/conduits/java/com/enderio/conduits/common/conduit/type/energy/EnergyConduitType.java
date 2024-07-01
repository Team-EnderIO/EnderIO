package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.NewConduitTypeSerializer;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.EIOConduitTypes;
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

import java.util.List;
import java.util.Set;

public record EnergyConduitType(
    ResourceLocation texture,
    Component description,
    int transferRate
) implements ConduitType<EnergyConduitType, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> {

    public static final MapCodec<EnergyConduitType> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder
            .group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ConduitType::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(ConduitType::description),
                Codec.INT.fieldOf("transfer_rate").forGetter(EnergyConduitType::transferRate)
            ).apply(builder, EnergyConduitType::of)
    );

    public static EnergyConduitType of (ResourceLocation texture, Component description, int transferRate) {
        return new EnergyConduitType(texture, description, transferRate);
    }

    private static final EnergyConduitTicker TICKER = new EnergyConduitTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    @Override
    public NewConduitTypeSerializer<EnergyConduitType> serializer() {
        return EIOConduitTypes.TypeSerializers.ENERGY.get();
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
    public EnergyConduitNetworkContext createNetworkContext(ConduitNetwork<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> network) {
        return new EnergyConduitNetworkContext();
    }

    @Override
    public ConduitData.EmptyConduitData createConduitData(Level level, BlockPos pos) {
        return ConduitData.EMPTY;
    }

    @Override
    public boolean canBeInSameBundle(Holder<ConduitType<?, ?, ?>> conduitType) {
        if (conduitType.value() instanceof EnergyConduitType) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canBeReplacedBy(Holder<ConduitType<?, ?, ?>> conduitType) {
        if (!(conduitType.value() instanceof EnergyConduitType energyConduitType)) {
            return false;
        }

        return this.transferRate() < energyConduitType.transferRate();
    }

    @Override
    public <K> @Nullable K proxyCapability(BlockCapability<K, Direction> capability, ConduitNode<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> node,
        Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {

        if (Capabilities.EnergyStorage.BLOCK == capability && (state == null || state.isExtract())) {
            //noinspection unchecked
            return (K) new EnergyConduitStorage(transferRate(), node);
        }

        return null;
    }

    @Override
    public Set<BlockCapability<?, Direction>> getExposedCapabilities() {
        return Set.of(Capabilities.EnergyStorage.BLOCK);
    }

    @Override
    public void onRemoved(ConduitData.EmptyConduitData data, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
        if (capability != null) {
            // Always default to both directions.
            return new ConduitConnectionData(true, true, RedstoneControl.ALWAYS_ACTIVE);
        }

        return ConduitType.super.getDefaultConnection(level, pos, direction);
    }

    @Override
    public List<Component> getHoverText(Item.TooltipContext context, TooltipFlag tooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRate());
        return List.of(TooltipUtil.styledWithArgs(ConduitLang.ENERGY_RATE_TOOLTIP, transferLimitFormatted));
    }

    @Override
    public int compareTo(@NotNull EnergyConduitType o) {
        if (transferRate() < o.transferRate()) {
            return -1;
        } else if (transferRate() > o.transferRate()) {
            return 1;
        }

        return 0;
    }
}
