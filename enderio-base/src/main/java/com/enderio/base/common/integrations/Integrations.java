package com.enderio.base.common.integrations;

import com.enderio.base.api.integration.IntegrationManager;

public class Integrations {

    public static void register() {
        IntegrationManager.addIntegration(EnderIOSelfIntegration.INSTANCE);
    }
}
