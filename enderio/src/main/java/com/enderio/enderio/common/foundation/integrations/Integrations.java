package com.enderio.enderio.common.foundation.integrations;

import com.enderio.enderio.api.integration.IntegrationManager;

public class Integrations {

    public static void register() {
        IntegrationManager.addIntegration(EnderIOSelfIntegration.INSTANCE);
    }
}
