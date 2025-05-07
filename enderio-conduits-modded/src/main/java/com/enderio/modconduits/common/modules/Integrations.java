package com.enderio.modconduits.common.modules;

import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.api.integration.IntegrationWrapper;
import com.enderio.modconduits.common.ModdedConduits;
import com.enderio.modconduits.common.modules.laserio.LaserIOIntegration;

public class Integrations {

    public static final IntegrationWrapper<LaserIOIntegration> LASER_IO_INTEGRATION = IntegrationManager
            .wrapper("laserio", () -> LaserIOIntegration::new, ModdedConduits.modEventBus);

    public static void register() {
    }
}
