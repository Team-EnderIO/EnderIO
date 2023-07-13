package com.enderio.conduits.common.integrations;

import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.integration.IntegrationWrapper;
import com.enderio.conduits.common.integrations.ae2.AE2Integration;

public class Integrations {

    public static final IntegrationWrapper<AE2Integration> ae2Integration = IntegrationManager.wrapper("ae2", AE2Integration::new);
    public static final IntegrationWrapper<ConduitSelfIntegration> selfIntegration = IntegrationManager.wrapper("enderio", ConduitSelfIntegration::new);

    public static void register() {
    }
}
