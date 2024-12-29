package com.enderio.conduits.common.integrations;

import com.enderio.EnderIOBase;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.api.integration.IntegrationWrapper;
import com.enderio.conduits.common.integrations.cctweaked.CCIntegration;

public class Integrations {

    public static final IntegrationWrapper<CCIntegration> CC_INTEGRATION = IntegrationManager.wrapper("computercraft",
            () -> CCIntegration::new, EnderIOBase.modEventBus);

    public static void register() {
    }
}
