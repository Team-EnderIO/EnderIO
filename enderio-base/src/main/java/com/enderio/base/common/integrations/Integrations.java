package com.enderio.base.common.integrations;

import com.enderio.EnderIOBase;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.api.integration.IntegrationWrapper;
import com.enderio.base.common.integrations.laserio.LaserIOIntegration;

public class Integrations {

    public static final IntegrationWrapper<LaserIOIntegration> LASER_IO_INTEGRATION = IntegrationManager.wrapper("laserio", () -> LaserIOIntegration::new, EnderIOBase.modEventBus);


    public static void register() {
        IntegrationManager.addIntegration(EnderIOSelfIntegration.INSTANCE);
    }
}
