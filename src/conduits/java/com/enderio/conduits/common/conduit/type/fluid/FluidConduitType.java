package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitTypeSerializer;
import com.enderio.api.conduit.NewConduitTypeSerializer;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.EIOConduitTypes;
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

import java.util.List;

public record FluidConduitType(
    ResourceLocation texture,
    Component description,
    int transferRate,
    boolean isMultiFluid
) implements ConduitType<FluidConduitType, ConduitNetworkContext.Dummy, FluidConduitData> {

    public static final MapCodec<FluidConduitType> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder
            .group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(FluidConduitType::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(FluidConduitType::description),
                Codec.INT.fieldOf("transfer_rate").forGetter(FluidConduitType::transferRate),
                Codec.BOOL.fieldOf("is_multi_fluid").forGetter(FluidConduitType::isMultiFluid)
            ).apply(builder, FluidConduitType::new)
    );

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, true, false, false, true);
    private static final FluidConduitTicker TICKER = new FluidConduitTicker();

    @Override
    public NewConduitTypeSerializer<FluidConduitType> serializer() {
        return EIOConduitTypes.TypeSerializers.FLUID.get();
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
    @Nullable
    public ConduitNetworkContext.Dummy createNetworkContext(ConduitNetwork<ConduitNetworkContext.Dummy, FluidConduitData> network) {
        return null;
    }

    @Override
    public FluidConduitData createConduitData(Level level, BlockPos pos) {
        return new FluidConduitData(isMultiFluid());
    }

    @Override
    public boolean canBeInSameBundle(Holder<ConduitType<?, ?, ?>> conduitType) {
        if (conduitType.value() instanceof FluidConduitType) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canBeReplacedBy(Holder<ConduitType<?, ?, ?>> conduitType) {
        if (!(conduitType.value() instanceof FluidConduitType fluidConduitType)) {
            return false;
        }

        // Replacement must support multi fluid if the current does.
        if (isMultiFluid() && !fluidConduitType.isMultiFluid()) {
            return false;
        }

        return transferRate() <= fluidConduitType.transferRate();
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
    public List<Component> getHoverText(Item.TooltipContext context, TooltipFlag tooltipFlag) {
        // Get transfer rate, adjusted for the ticker rate.
        String transferLimitFormatted = String.format("%,d", transferRate() * (20 / getTicker().getTickRate()));
        Component rateTooltip = TooltipUtil.styledWithArgs(ConduitLang.FLUID_RATE_TOOLTIP, transferLimitFormatted);

        if (isMultiFluid()) {
            return List.of(rateTooltip, ConduitLang.MULTI_FLUID_TOOLTIP);
        }

        return List.of(rateTooltip);
    }

    @Override
    public int compareTo(@NotNull FluidConduitType o) {
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
