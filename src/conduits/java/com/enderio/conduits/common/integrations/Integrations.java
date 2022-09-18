package com.enderio.conduits.common.integrations;

import com.enderio.EnderIO;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.integration.IntegrationWrapper;

public class Integrations {

    public static final IntegrationWrapper<AE2Integration> ae2Integration = IntegrationManager.wrapper("ae2", AE2Integration::new);

    public static void register() {
    }
}
