package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.ListDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.*;

public final class ConduitBundle {

    public static final int MAX_CONDUIT_TYPES = 16;

    private final Map<Direction, ConduitConnection> connections = new EnumMap<>(Direction.class);

    private final List<IConduitType> types = new ArrayList<>();
    private final Runnable scheduleSync;

    public ConduitBundle(Runnable scheduleSync) {
        this.scheduleSync = scheduleSync;
        for (Direction value : Direction.values()) {
            connections.put(value, new ConduitConnection(this));
        }
    }

    /**
     * @param type
     * @return the type that is now not in this bundle
     */
    public Optional<IConduitType> addType(IConduitType type) {
        if (types.size() == MAX_CONDUIT_TYPES)
            return Optional.of(type);
        if (types.contains(type))
            return Optional.of(type);
        //upgrade a conduit
        Optional<IConduitType> first = types.stream().filter(existingConduit -> existingConduit.canBeReplacedBy(type)).findFirst();
        if (first.isPresent()) {
            int index = types.indexOf(first.get());
            types.set(index, type);
            scheduleSync.run();
            return first;
        }
        //some conduit says no (like higher energy conduit)
        if (types.stream().anyMatch(existingConduit -> !existingConduit.canBeInSameBlock(type)))
            return Optional.of(type);
        //sort the list, so order is consistant
        int id = ConduitTypes.getRegistry().getID(type);
        var addBefore = types.stream().map(existing -> ConduitTypes.getRegistry().getID(existing)).filter(existingID -> existingID > id).findFirst();
        if (addBefore.isPresent()) {
            types.add(addBefore.get(), type);
            for (Direction direction: Direction.values()) {
                connections.get(direction).addType(addBefore.get());
            }
        } else {
            types.add(type);
        }
        scheduleSync.run();
        return Optional.empty();
    }

    /**
     * @param type
     * @throws IllegalArgumentException if this type is not in the conduitbundle and we are in dev env
     * @return if this bundle is empty and the block has to be removed
     */
    public boolean removeType(IConduitType type) {
        int index = types.indexOf(type);
        if (index == -1) {
            if (!FMLLoader.isProduction()) {
                throw new IllegalArgumentException("Conduit: " + ConduitTypes.REGISTRY.get().getKey(type) + "is not present in conduit bundle "
                    + Arrays.toString(types.stream().map(existingType -> ConduitTypes.REGISTRY.get().getKey(existingType)).toArray()));
            }
            return types.isEmpty();
        }
        for (Direction direction: Direction.values()) {
            connections.get(direction).removeType(index);
        }
        types.remove(index);
        scheduleSync.run();
        return types.isEmpty();
    }

    public List<EnderDataSlot<?>> gatherDataSlots() {
        List<EnderDataSlot<?>> dataSlots = new ArrayList<>();

        dataSlots.add(new ListDataSlot<>(
            () -> types,
            type -> IntTag.valueOf(ConduitTypes.getRegistry().getID(type)),
            intTag -> ConduitTypes.getRegistry().getValue(intTag.getAsInt()),
            SyncMode.WORLD
        ));
        connections.values().forEach(connection -> connection.gatherDataSlots(dataSlots));
        return dataSlots;
    }
}
