package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.EnderIO;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record FluidConduit(
    ResourceLocation texture,
    Component description,
    int transferRate,
    boolean isMultiFluid
) implements Conduit<FluidConduit> {

    public static final MapCodec<FluidConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder
            .group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(FluidConduit::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(FluidConduit::description),
                Codec.INT.fieldOf("transfer_rate").forGetter(FluidConduit::transferRate),
                Codec.BOOL.fieldOf("is_multi_fluid").forGetter(FluidConduit::isMultiFluid)
            ).apply(builder, FluidConduit::new)
    );

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, true, false, false, true);
    private static final FluidConduitTicker TICKER = new FluidConduitTicker();

    @Override
    public ConduitType<FluidConduit> type() {
        return ConduitTypes.FLUID.get();
    }

    @Override
    public FluidConduitTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public boolean canBeInSameBundle(Holder<Conduit<?>> otherConduit) {
        return !(otherConduit.value() instanceof FluidConduit);
    }

    @Override
    public boolean canBeReplacedBy(Holder<Conduit<?>> otherConduit) {
        if (!(otherConduit.value() instanceof FluidConduit otherFluidConduit)) {
            return false;
        }

        return compareTo(otherFluidConduit) > 0;
    }

    @Override
    public boolean canConnectTo(ConduitNode selfNode, ConduitNode otherNode) {
        FluidConduitData selfData = selfNode.getOrCreateData(ConduitTypes.Data.FLUID.get());
        FluidConduitData otherData = otherNode.getOrCreateData(ConduitTypes.Data.FLUID.get());

        return selfData.lockedFluid() == null || otherData.lockedFluid() == null || selfData.lockedFluid() == otherData.lockedFluid();
    }

    @Override
    public void onConnectTo(ConduitNode selfNode, ConduitNode otherNode) {
        FluidConduitData selfData = selfNode.getOrCreateData(ConduitTypes.Data.FLUID.get());
        FluidConduitData otherData = otherNode.getOrCreateData(ConduitTypes.Data.FLUID.get());

        if (selfData.lockedFluid() != null) {
            if (otherData.lockedFluid() != null && selfData.lockedFluid() != otherData.lockedFluid()) {
                EnderIO.LOGGER.warn("incompatible fluid conduits merged");
            }

            otherData.setLockedFluid(selfData.lockedFluid());
        } else if (otherData.lockedFluid() != null) {
            selfData.setLockedFluid(otherData.lockedFluid());
        }
    }

    @Override
    public boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof ExtractionSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return resourceFilter instanceof FluidStackFilter;
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRate() * (20 / getTicker().getTickRate()));
        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RATE_TOOLTIP, transferLimitFormatted));

        if (isMultiFluid()) {
            pTooltipAdder.accept(ConduitLang.MULTI_FLUID_TOOLTIP);
        }
    }

    @Override
    public int compareTo(@NotNull FluidConduit o) {
        if (isMultiFluid() && !o.isMultiFluid()) {
            return 1;
        }

        if (transferRate() < o.transferRate()) {
            return -1;
        } else if (transferRate() > o.transferRate()) {
            return 1;
        }

        return 0;
    }
}
