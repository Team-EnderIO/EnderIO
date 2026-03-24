package com.enderio.core.common.storage.slot;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.Collection;

// TODO: misleading name? It can represent one or more resource slots...
public sealed interface ResourceSlotKey<T extends Resource>
    extends Iterable<ResourceSlotId<T>>
    permits SingleResourceSlotKey, MultiResourceSlotKey {

    Collection<ResourceSlotId<T>> slots();
}
