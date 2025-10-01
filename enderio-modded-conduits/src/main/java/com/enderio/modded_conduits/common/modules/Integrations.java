package com.enderio.modded_conduits.common.modules;

import com.enderio.enderio.api.integration.IntegrationManager;
import com.enderio.enderio.api.integration.IntegrationWrapper;
import com.enderio.modded_conduits.ModdedConduits;
import com.enderio.modded_conduits.common.modules.laserio.LaserIOIntegration;

public class Integrations {

    public static final IntegrationWrapper<LaserIOIntegration> LASER_IO_INTEGRATION = IntegrationManager
            .wrapper("laserio", () -> LaserIOIntegration::new, ModdedConduits.modEventBus);

    public static void register() {
    }
}
