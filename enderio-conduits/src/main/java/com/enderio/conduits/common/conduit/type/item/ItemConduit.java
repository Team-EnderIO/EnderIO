package com.enderio.conduits.common.conduit.type.item;

import com.enderio.base.api.filter.ItemStackFilter;
import com.enderio.base.api.filter.ResourceFilter;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.SlotType;
import com.enderio.conduits.api.upgrade.ConduitUpgrade;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public record ItemConduit(
    ResourceLocation texture,
    Component description,
    int transferRate,
    int tickRate
) implements Conduit<ItemConduit> {

    public static final MapCodec<ItemConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder
            .group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ItemConduit::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(ItemConduit::description),
                Codec.INT.fieldOf("transfer_rate").forGetter(ItemConduit::transferRate),
                Codec.intRange(1, 20).fieldOf("tick_rate").forGetter(ItemConduit::tickRate)
            ).apply(builder, ItemConduit::new)
    );

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, true, true, true, true);

    private static final Map<Integer, ItemConduitTicker> TICKERS = new HashMap<>();

    @Override
    public ConduitType<ItemConduit> type() {
        return ConduitTypes.ITEM.get();
    }

    @Override
    public ItemConduitTicker getTicker() {
        return TICKERS.computeIfAbsent(tickRate(), ItemConduitTicker::new);
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof ExtractionSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return resourceFilter instanceof ItemStackFilter;
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRate());
        String tickRateFormatted = String.format("%,.2f", getTicker().getTickRate() / 20.0);
        String calculatedTransferLimitFormatted = String.format("%,d", (int)Math.floor(transferRate() * (20.0 / getTicker().getTickRate())));

        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.ITEM_CONDUIT_TRANSFER_TOOLTIP, transferLimitFormatted));
        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.ITEM_CONDUIT_CYCLE_TOOLTIP, tickRateFormatted));
        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.ITEM_CONDUIT_CALCULATED_TOOLTIP, calculatedTransferLimitFormatted));
    }

    @Override
    public int compareTo(@NotNull ItemConduit o) {
        double selfEffectiveSpeed = transferRate() * (20.0 / tickRate());
        double otherEffectiveSpeed = o.transferRate() * (20.0 / o.tickRate());

        if (selfEffectiveSpeed < otherEffectiveSpeed) {
            return -1;
        } else if (selfEffectiveSpeed > otherEffectiveSpeed) {
            return 1;
        }

        return 0;
    }
}
