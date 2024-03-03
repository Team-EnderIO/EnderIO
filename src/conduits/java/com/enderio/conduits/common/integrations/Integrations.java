package com.enderio.conduits.common.integrations;

import com.enderio.EnderIO;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.integration.IntegrationWrapper;
import com.enderio.conduits.common.integrations.ae2.AE2Integration;
import com.enderio.conduits.common.integrations.mekanism.MekanismIntegration;

public class Integrations {

    public static final IntegrationWrapper<AE2Integration> AE2_INTEGRATION = IntegrationManager.wrapper("ae2", AE2Integration::new, EnderIO.modEventBus);
    public static final IntegrationWrapper<MekanismIntegration> MEKANISM_INTEGRATION = IntegrationManager.wrapper("mekanism", MekanismIntegration::new, EnderIO.modEventBus);
    public static final IntegrationWrapper<ConduitSelfIntegration> SELF_INTEGRATION = IntegrationManager.wrapper("enderio", ConduitSelfIntegration::new, EnderIO.modEventBus);

    public static void register() {
    }
}
