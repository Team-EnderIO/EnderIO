package com.enderio.modconduits.common.modules.mekanism.heat;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record HeatConduit(ResourceLocation texture, Component description)
        implements Conduit<HeatConduit, HeatConduitConnectionConfig> {

    private static final HeatTicker TICKER = new HeatTicker();

    @Override
    public ConduitType<HeatConduit> type() {
        return MekanismModule.TYPE_HEAT.get();
    }

    @Override
    public HeatTicker ticker() {
        return TICKER;
    }

    @Override
    public ConnectionConfigType<HeatConduitConnectionConfig> connectionConfigType() {
        return HeatConduitConnectionConfig.TYPE;
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        return level.getCapability(MekanismModule.Capabilities.HEAT, conduitPos.relative(direction),
                direction.getOpposite()) != null;
    }

    @Override
    public int compareTo(@NotNull HeatConduit o) {
        return 0;
    }
}
