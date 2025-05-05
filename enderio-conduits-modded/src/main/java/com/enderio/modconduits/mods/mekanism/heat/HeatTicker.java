package com.enderio.modconduits.mods.mekanism.heat;

import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.network.ConduitNetwork;
import com.enderio.conduits.api.network.node.ConduitNode;
import com.enderio.conduits.api.ticker.IOAwareConduitTicker;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeatTicker extends IOAwareConduitTicker<HeatConduit, HeatConduitConnectionConfig, HeatTicker.Connection> {

    public HeatTicker() {
    }

    @Override
    protected void tickColoredGraph(ServerLevel level, HeatConduit conduit, List<Connection> senders, List<Connection> receivers, DyeColor color,
        ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        for (var receiver : receivers) {
            IHeatHandler extractHandler = receiver.heatHandler();

            for (var sender : senders) {
                IHeatHandler insertHandler = sender.heatHandler();

                double heatCapacity = extractHandler.getTotalHeatCapacity();
                double invConduction = insertHandler.getTotalInverseConduction() + extractHandler.getTotalInverseConduction();
                double tempToTransfer = (extractHandler.getTotalTemperature() - insertHandler.getTotalTemperature())
                    / invConduction; //TODO subtract ambient? - HeatAPI.getAmbientTemp(level, )

                double heatToTransfer = tempToTransfer * heatCapacity;
                if (heatToTransfer > 0) {
                    extractHandler.handleHeat(-heatToTransfer);
                    insertHandler.handleHeat(heatToTransfer);
                }
            }
        }
    }

    @Override
    protected @Nullable HeatTicker.Connection createConnection(Level level, ConduitNode node, Direction side) {
//        var heatHandler = level.getCapability(MekanismModule.Capabilities.HEAT, node.getPos().relative(side), side.getOpposite());
//        if (heatHandler != null) {
//            return new Connection(node, side, node.getConnectionConfig(side, HeatConduitConnectionConfig.TYPE), heatHandler);
//        }
        return null;
    }

    protected static class Connection extends SimpleConnection<HeatConduitConnectionConfig> {
        private final IHeatHandler heatHandler;

        public Connection(ConduitNode node, Direction side, HeatConduitConnectionConfig config, IHeatHandler heatHandler) {
            super(node, side, config);
            this.heatHandler = heatHandler;
        }

        public IHeatHandler heatHandler() {
            return heatHandler;
        }
    }
}
