package com.enderio.enderio.foundation.storage;

import com.enderio.enderio.api.io.IOConfigurable;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public final class SidedResourceHandler<T extends Resource> implements ResourceHandler<T> {
    private final ResourceHandler<T> handler;
    private final Direction face;
    private final IOConfigurable config;

    // TODO: Move this to a handler utility class?
    @Nullable
    public static <T extends Resource> ResourceHandler<T> of(ResourceHandler<T> handler, @Nullable Direction face, IOConfigurable config) {
        if (face == null) {
            return handler;
        }

        if (config.getIOMode(face).canConnect()) {
            return new SidedResourceHandler<>(handler, face, config);
        }

        return null;
    }

    private SidedResourceHandler(ResourceHandler<T> handler, Direction face, IOConfigurable config) {
        this.handler = handler;
        this.face = face;
        this.config = config;
    }

    @Override
    public int size() {
        return handler.size();
    }

    @Override
    public T getResource(int index) {
        return handler.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index) {
        return handler.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        return handler.getCapacityAsLong(index, resource);
    }

    @Override
    public boolean isValid(int index, T resource) {
        return handler.isValid(index, resource);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        if (config.getIOMode(face).canInput()) {
            return handler.insert(index, resource, amount, transaction);
        }

        return 0;
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        if (config.getIOMode(face).canOutput()) {
            return handler.extract(index, resource, amount, transaction);
        }

        return 0;
    }
}
