package com.enderio.api;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitDataType;
import org.jetbrains.annotations.Nullable;

public interface ConduitDataAccessor {
    boolean hasData(ConduitDataType<?> type);

    // TODO: I want to add setData at some point and maybe enforce conduit data as records?

    /**
     * Get the data attached to this node.
     * @param type The expected data type.
     * @return The attached data or null if there is no data -or- the data is of a different type.
     */
    @Nullable
    <T extends ConduitData<T>> T getData(ConduitDataType<T> type);

    /**
     * Gets the data attached to this node.
     * @param type The data type that is expected.
     * @return The stored data that matched this type, or a new instance of the data.
     * @implNote If a conduit data of a different type exists in this node, it will be replaced.
     */
    <T extends ConduitData<T>> T getOrCreateData(ConduitDataType<T> type);

    //<T extends ConduitData<T>> T setData(T data);
}
