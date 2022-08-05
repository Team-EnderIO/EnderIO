package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.core.Direction;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.*;

public final class ConduitBundle {

    public static final int MAX_CONDUIT_TYPES = 16;

    private final Map<Direction, ConduitConnection> connections = new EnumMap<>(Direction.class);

    private final List<IConduitType> types = new ArrayList<>();


    public ConduitBundle() {
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
        return types.isEmpty();
    }

    public List<EnderDataSlot<?>> gatherDataSlots() {
        List<EnderDataSlot<?>> dataSlots = new ArrayList<>();

        //TODO: create a listdataslot to solve this mess
        for (int i = 0; i < MAX_CONDUIT_TYPES; i++) {
            int finalI = i;
            dataSlots.add(new IntegerDataSlot(
                () -> {
                    if (finalI >= types.size())
                        return -1;
                    return ConduitTypes.getRegistry().getID(types.get(finalI));
                },
                id -> {
                    if (id == -1) {
                        if (finalI < types.size()) {
                            //remove as many elements as we need so if finalI = 0, list shrinks to no elements
                            int toRemove = types.size() - finalI;
                            for (int j = 0; j < toRemove; j++) {
                                types.remove(types.size());
                            }
                        }
                    } else {
                        IConduitType type = ConduitTypes.getRegistry().getValue(id);
                        if (finalI < types.size()) {
                            types.set(finalI, type);
                        } else if (finalI == types.size()) {
                            types.add(type);
                        } else {
                            //TODO don't throw exceptions in dataslots, maybe fixed in the coming listdataslot impl
                            throw new IllegalArgumentException("Should never come here, as previous dataslots are synced first, so they should have atleast this size");
                        }
                    }
                },
                SyncMode.WORLD
            ));
        }
        connections.values().forEach(connection -> connection.gatherDataSlots(dataSlots));
        return dataSlots;
    }
}
