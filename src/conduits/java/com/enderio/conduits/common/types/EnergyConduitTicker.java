package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class EnergyConduitTicker extends CapabilityAwareConduitTicker<IEnergyStorage> {

    public EnergyConduitTicker() {
    }

    @Override
    public void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level,
        Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {

        toNextExtract:
        for (CapabilityConnection extract : extracts) {
            IEnergyStorage extractHandler = extract.cap;
            int availableForExtraction = extractHandler.extractEnergy(Integer.MAX_VALUE, true);
            if (availableForExtraction <= 0)
                continue;
            EnergyExtendedData.EnergySidedData sidedExtractData = extract.data.castTo(EnergyExtendedData.class).compute(extract.direction);

            if (inserts.size() <= sidedExtractData.rotatingIndex) {
                sidedExtractData.rotatingIndex = 0;
            }

            for (int j = sidedExtractData.rotatingIndex; j < sidedExtractData.rotatingIndex + inserts.size(); j++) {
                int insertIndex = j % inserts.size();
                CapabilityConnection insert = inserts.get(insertIndex);

                int inserted = insert.cap.receiveEnergy(availableForExtraction, false);
                extractHandler.extractEnergy(inserted, false);

                if (inserted == availableForExtraction) {
                    sidedExtractData.rotatingIndex += (insertIndex) + 1;
                    continue toNextExtract;
                }

                availableForExtraction -= inserted;
            }
        }
    }

    /**
     * This ensures consistent behaviour for FE/t caps and more.
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }
}
