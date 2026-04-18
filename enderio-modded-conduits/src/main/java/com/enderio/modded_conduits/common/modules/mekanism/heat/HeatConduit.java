//package com.enderio.modded_conduits.common.modules.mekanism.heat;
//
//import com.enderio.enderio.api.conduits.Conduit;
//import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
//import com.enderio.enderio.api.conduits.ConduitType;
//import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
//import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.NotNull;
//
//public record HeatConduit(ResourceLocation texture, Component description)
//        implements Conduit<HeatConduit, HeatConduitConnectionConfig> {
//
//    @Override
//    public ConduitType<HeatConduit, HeatConduitConnectionConfig> type() {
//        return MekanismModule.TYPE_HEAT.get();
//    }
//
//    @Override
//    public boolean hasMenu() {
//        return true;
//    }
//
//    @Override
//    public boolean shouldCheckConnectionsOnNeighborChange() {
//        return false;
//    }
//
//    @Override
//    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
//        return capabilityAccessor.getSidedCapability(MekanismModule.Capabilities.HEAT, direction) != null;
//    }
//
//    @Override
//    public int compareTo(@NotNull HeatConduit o) {
//        return 0;
//    }
//}
