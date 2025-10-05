package com.enderio.enderio.foundation.integrations;

import com.enderio.enderio.api.integration.IntegrationManager;

public class Integrations {

    public static void register() {
        IntegrationManager.addIntegration(EnderIOSelfIntegration.INSTANCE);
    }
}
