package com.enderio.api.conduit.ticker;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ICapabilityAwareConduitTicker<T> extends IIOAwareConduitTicker {

    @Override
    default void tickColoredGraph(List<ConnectorPos> inserts, List<ConnectorPos> extracts, ServerLevel level) {
        List<T> insertCaps = new ArrayList<>();
        for (ConnectorPos insert : inserts) {
            Optional.ofNullable(level.getBlockEntity(insert.move())).flatMap(b -> b.getCapability(getCapability(), insert.dir().getOpposite()).resolve()).ifPresent(insertCaps::add);
        }
        if (!insertCaps.isEmpty()) {
            List<T> extractCaps = new ArrayList<>();

            for (ConnectorPos extract : extracts) {
                Optional.ofNullable(level.getBlockEntity(extract.move())).flatMap(b -> b.getCapability(getCapability(), extract.dir().getOpposite()).resolve()).ifPresent(extractCaps::add);
            }
            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(insertCaps, extractCaps, level);
            }
        }
    }

    void tickCapabilityGraph(List<T> inserts, List<T> extracts, ServerLevel level);

    Capability<T> getCapability();
}
