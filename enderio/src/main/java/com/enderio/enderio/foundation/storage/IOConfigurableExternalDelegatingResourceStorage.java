package com.enderio.enderio.foundation.storage;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.delegates.ExternalDelegatingResourceStorage;
import com.enderio.core.common.storage.delegates.ExternalResourceHandlerSlotConfig;
import com.enderio.enderio.api.io.IOConfigurable;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class IOConfigurableExternalDelegatingResourceStorage<T extends Resource> extends ExternalDelegatingResourceStorage<T> {
    private final Direction face;
    private final IOConfigurable config;

    // TODO: Move this to a handler utility class?
    @Nullable
    public static <T extends Resource> ResourceHandler<T> of(ResourceStorage<T> handler, @Nullable Direction face, IOConfigurable config) {
        if (face == null) {
            return handler;
        }

        if (config.getIOMode(face).canConnect()) {
            return new IOConfigurableExternalDelegatingResourceStorage<>(handler, face, config);
        }

        return null;
    }

    private IOConfigurableExternalDelegatingResourceStorage(
        ResourceStorage<T> delegate,
        @Nullable Direction face,
        IOConfigurable config) {
        super(delegate);
        this.face = face;
        this.config = config;
    }

    private IOConfigurableExternalDelegatingResourceStorage(
        Supplier<ResourceStorage<T>> delegate,
        @Nullable Direction face,
        IOConfigurable config) {
        super(delegate);
        this.face = face;
        this.config = config;
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        if (face != null && !config.getIOMode(face).canInput()) {
            return 0;
        }

        return super.insert(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        if (face != null && !config.getIOMode(face).canOutput()) {
            return 0;
        }

        return super.extract(index, resource, amount, transaction);
    }
}
