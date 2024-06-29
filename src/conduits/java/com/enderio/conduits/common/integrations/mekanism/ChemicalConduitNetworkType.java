package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
        if (conduitType.graphType() != this) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(ChemicalConduitOptions options, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.graphType() != this) {
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

    // TODO: Support for extract upgrades
}
