//package com.enderio.modded_conduits.common.modules.mekanism.heat;
//
//import com.enderio.enderio.api.conduits.Conduit;
//import com.enderio.enderio.api.conduits.ConduitType;
//import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
//import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.level.Level;
//import org.jspecify.annotations.NonNull;
//
//public record HeatConduit(Identifier texture, Component description)
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
//    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
//        return level.getCapability(MekanismModule.Capabilities.HEAT, conduitPos.relative(direction),
//                direction.getOpposite()) != null;
//    }
//
//    @Override
//    public int compareTo(@NonNull HeatConduit o) {
//        return 0;
//    }
//}
