package com.enderio.enderio.api.soul;

import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.ServiceLoader;

// Leaving as internal for the moment - unsure if this is how we want to expose this functionality.
// It is just required for the migration to a separate source set.
@ApiStatus.Internal
public interface SoulCaptureApi {
    SoulCaptureApi INSTANCE = ServiceLoader.load(SoulCaptureApi.class).findFirst().orElseThrow();

    List<EntityType<?>> getCapturableEntityTypes();
}
