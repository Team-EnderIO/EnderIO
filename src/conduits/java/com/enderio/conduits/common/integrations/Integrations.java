package com.enderio.conduits.common.integrations;

import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.integration.IntegrationWrapper;

public class Integrations {

    //public static final IntegrationWrapper<AE2Integration> AE2_INTEGRATION = IntegrationManager.wrapper("ae2", AE2Integration::new); TODO
    public static final IntegrationWrapper<ConduitSelfIntegration> SELF_INTEGRATION = IntegrationManager.wrapper("enderio", ConduitSelfIntegration::new);

    public static void register() {
    }
}
