package com.enderio.enderio.conduits.common.integrations;

import com.enderio.EnderIOBase;
import com.enderio.enderio.api.integration.IntegrationManager;
import com.enderio.enderio.api.integration.IntegrationWrapper;
import com.enderio.enderio.conduits.common.integrations.cctweaked.CCIntegration;

public class Integrations {

    public static final IntegrationWrapper<CCIntegration> CC_INTEGRATION = IntegrationManager.wrapper("computercraft",
            () -> CCIntegration::new, EnderIOBase.modEventBus);

    public static void register() {
    }
}
