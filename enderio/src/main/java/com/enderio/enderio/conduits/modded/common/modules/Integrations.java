package com.enderio.enderio.conduits.modded.common.modules;

import com.enderio.enderio.api.integration.IntegrationManager;
import com.enderio.enderio.api.integration.IntegrationWrapper;
import com.enderio.enderio.conduits.modded.common.ModdedConduits;
import com.enderio.enderio.conduits.modded.common.modules.laserio.LaserIOIntegration;

public class Integrations {

    public static final IntegrationWrapper<LaserIOIntegration> LASER_IO_INTEGRATION = IntegrationManager
            .wrapper("laserio", () -> LaserIOIntegration::new, ModdedConduits.modEventBus);

    public static void register() {
    }
}
