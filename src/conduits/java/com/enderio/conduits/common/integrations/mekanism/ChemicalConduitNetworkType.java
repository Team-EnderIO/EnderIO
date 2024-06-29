package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChemicalConduitNetworkType implements ConduitNetworkType<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData> {

    private static final ChemicalTicker TICKER = new ChemicalTicker(MekanismIntegration.GAS, MekanismIntegration.SLURRY, MekanismIntegration.INFUSION, MekanismIntegration.PIGMENT);

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    private static final ConduitMenuData MULTI_MENU_DATA = new ConduitMenuData.Simple(false, false, false, true, true, true);

    @Override
    public ChemicalTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData(ChemicalConduitOptions options) {
        return options.isMultiChemical() ? MULTI_MENU_DATA : MENU_DATA;
    }

    @Override
    public @Nullable ConduitNetworkContext.Dummy createNetworkContext(
        ConduitType<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData> type,
        ConduitNetwork<ConduitNetworkContext.Dummy, ChemicalConduitData> network) {
        return null;
    }

    @Override
    public ChemicalConduitData createConduitData(ConduitType<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData> type, Level level,
        BlockPos pos) {
        return new ChemicalConduitData(type.options().isMultiChemical());
    }

    @Override
    public boolean canBeInSameBundle(ChemicalConduitOptions options, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.networkType() != this) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(ChemicalConduitOptions options, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.networkType() != this) {
            return false;
        }

        if (conduitType.options() instanceof ChemicalConduitOptions otherOptions) {
            // Replacement must support multi fluid if the current does.
            if (options.isMultiChemical() && !otherOptions.isMultiChemical()) {
                return false;
            }

            return options.transferRate() <= otherOptions.transferRate();
        }

        return false;
    }

    @Override
    public List<Component> getHoverText(ChemicalConduitOptions options, Item.TooltipContext context, TooltipFlag tooltipFlag) {
        // Get transfer rate, adjusted for the ticker rate.
        String transferLimitFormatted = String.format("%,d", options.transferRate() * (20 / getTicker().getTickRate()));
        Component rateTooltip = TooltipUtil.styledWithArgs(ConduitLang.FLUID_RATE_TOOLTIP, transferLimitFormatted);

        if (options.isMultiChemical()) {
            return List.of(rateTooltip, MekanismIntegration.LANG_MULTI_CHEMICAL_TOOLTIP);
        }

        return List.of(rateTooltip);
    }

    @Override
    public int compare(ChemicalConduitOptions o1, ChemicalConduitOptions o2) {
        if (o1.isMultiChemical() && !o2.isMultiChemical()) {
            return 1;
        }

        if (o1.transferRate() < o2.transferRate()) {
            return -1;
        } else if (o1.transferRate() > o2.transferRate()) {
            return 1;
        }

        return 0;
    }

    // TODO: Support for extract upgrades
}
