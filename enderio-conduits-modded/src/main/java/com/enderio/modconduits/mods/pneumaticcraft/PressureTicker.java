package com.enderio.modconduits.mods.pneumaticcraft;

import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.ConduitNetwork;
import com.enderio.conduits.api.ticker.CapabilityAwareConduitTicker;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PressureTicker extends CapabilityAwareConduitTicker<PressureConduit, IAirHandlerMachine> {

    public PressureTicker() {}

    @Override
    protected void tickCapabilityGraph(ServerLevel level, PressureConduit conduit,
        List<CapabilityAwareConduitTicker<PressureConduit, IAirHandlerMachine>.CapabilityConnection> inserts,
        List<CapabilityAwareConduitTicker<PressureConduit, IAirHandlerMachine>.CapabilityConnection> extracts, ConduitNetwork graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        PressureConduitContext context = graph.getContext(PneumaticModule.NetworkContexts.PRESSURE_NETWORK.get());
        if (context == null) {
            return;
        }

        Set<IAirHandlerMachine.Connection> neighbours = new HashSet<>();
        for (var insert : inserts) {
            IAirHandlerMachine handler = insert.capability();
            neighbours.add(new ConnectedAirHandler(insert.direction(), handler));
        }

        for (var extract : extracts) {
            IAirHandlerMachine handler = extract.capability();
            neighbours.add(new ConnectedAirHandler(extract.direction(), handler));
        }

        // 2. get the total volume and air amount in this and all connected handlers
        int totalVolume = graph.getNodes().size() * 1000;
        int totalAir = context.getAir();
        for (IAirHandlerMachine.Connection neighbour: neighbours) {
            totalVolume += neighbour.getAirHandler().getVolume();
            totalAir += neighbour.getAirHandler().getAir();
        }

        // 3. figure out how much air will be dispersed to each neighbour
        for (IAirHandlerMachine.Connection neighbour: neighbours) {
            int totalMachineAir = (int) ((long) totalAir * neighbour.getAirHandler().getVolume() / totalVolume);
            neighbour.setMaxDispersion(getMaxDispersion(neighbour.getDirection()));
            neighbour.setAirToDisperse(Math.max(0, totalMachineAir - neighbour.getAirHandler().getAir()));  // no backflow
        }

        // 4. finally, actually disperse the air
        for (IAirHandlerMachine.Connection neighbour : neighbours) {
            int air = Math.min(neighbour.getMaxDispersion(), neighbour.getDispersedAir());
            if (air != 0) {
                onAirDispersion(neighbour.getDirection(), air);
                neighbour.getAirHandler().addAir(air);
                addAir(-air, context);
            }
        }
    }

    @Override
    public boolean shouldSkipColor(List<Connection> extractList, List<Connection> insertList) {
        return false;
    }

    private void addAir(int i, PressureConduitContext context) {
        context.setAir(context.getAir() + i);
    }

    private void onAirDispersion(@Nullable Direction direction, int air) {

    }

    private int getMaxDispersion(@Nullable Direction direction) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected BlockCapability<IAirHandlerMachine, Direction> getCapability() {
        return PNCCapabilities.AIR_HANDLER_MACHINE;
    }

    private static class ConnectedAirHandler implements IAirHandlerMachine.Connection {
        final @Nullable Direction direction;
        final IAirHandlerMachine airHandler;
        int maxDispersion;
        int toDisperse;

        ConnectedAirHandler(@Nullable Direction direction, IAirHandlerMachine airHandler) {
            this.direction = direction;
            this.airHandler = airHandler;
        }

        @Override
        @Nullable
        public Direction getDirection() {
            return direction;
        }

        @Override
        public int getMaxDispersion() {
            return maxDispersion;
        }

        @Override
        public void setMaxDispersion(int maxDispersion) {
            this.maxDispersion = maxDispersion;
        }

        @Override
        public int getDispersedAir() {
            return toDisperse;
        }

        @Override
        public void setAirToDisperse(int toDisperse) {
            this.toDisperse = toDisperse;
        }

        @Override
        public IAirHandlerMachine getAirHandler() {
            return airHandler;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ConnectedAirHandler that = (ConnectedAirHandler) o;
            return direction == that.direction && airHandler.equals(that.airHandler);
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(direction);
            result = 31 * result + airHandler.hashCode();
            return result;
        }
    }
}
