package com.enderio.enderio.common.soul;

import com.enderio.enderio.api.soul.SoulCaptureApi;
import com.enderio.enderio.common.util.EntityCaptureUtils;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class SoulCaptureApiImpl implements SoulCaptureApi {
    @Override
    public List<EntityType<?>> getCapturableEntityTypes() {
        return EntityCaptureUtils.getCapturableEntityTypes();
    }
}
