package com.enderio.conduits.common.integrations.pneumaticcraft;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.conduit.ticker.ConduitTicker;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;

public class PressureTicker extends CapabilityAwareConduitTicker<ConduitData.EmptyConduitData, IAirHandlerMachine> {

    public static final ConduitTicker<ConduitData.EmptyConduitData> INSTANCE = new PressureTicker();

    @Override
    protected void tickCapabilityGraph(ServerLevel level, ConduitType<ConduitData.EmptyConduitData> type,
        List<CapabilityAwareConduitTicker<ConduitData.EmptyConduitData, IAirHandlerMachine>.CapabilityConnection> inserts,
        List<CapabilityAwareConduitTicker<ConduitData.EmptyConduitData, IAirHandlerMachine>.CapabilityConnection> extracts, ConduitGraph<ConduitData.EmptyConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (var extract: extracts) {
            for (var insert: inserts) {
                IAirHandlerMachine exCap = extract.capability;
                IAirHandlerMachine inCap = insert.capability;

                if (exCap.getPressure() < inCap.getPressure()) {
                    continue;
                }

                float newPressure = (inCap.getAir() + exCap.getAir()) /((float)(inCap.getVolume() + exCap.getVolume()));
                if (newPressure > inCap.getCriticalPressure()) {
                    float maxAir = inCap.getCriticalPressure() * inCap.getVolume();
                    int movedAir = (int) (maxAir - inCap.getAir());
                    inCap.addAir(movedAir);
                    exCap.addAir(movedAir);
                } else {
                    inCap.setPressure(newPressure);
                    exCap.setPressure(newPressure);
                }
            }
        }
    }

    @Override
    protected Capability<IAirHandlerMachine> getCapability() {
        return PNCCapabilities.AIR_HANDLER_MACHINE_CAPABILITY;
    }
}
