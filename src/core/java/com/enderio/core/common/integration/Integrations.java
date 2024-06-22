package com.enderio.core.common.integration;

import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.integration.IntegrationWrapper;

public class Integrations {

    public static final IntegrationWrapper<AlmostUnifiedIntegration> ALMOST_UNIFIED_INTEGRATION = IntegrationManager.wrapper("almostunified", () -> AlmostUnifiedIntegration::new);

    public static void register() {
    }
}
