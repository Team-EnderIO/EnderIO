package com.enderio.conduits.common.integrations;

import com.enderio.EnderIOBase;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.api.integration.IntegrationWrapper;
import com.enderio.conduits.common.integrations.cctweaked.CCIntegration;

public class Integrations {

    //public static final IntegrationWrapper<MekanismIntegration> MEKANISM_INTEGRATION = IntegrationManager.wrapper("mekanism", () -> MekanismIntegration::new, EnderIO.modEventBus);
    //public static final IntegrationWrapper<AE2Integration> AE2_INTEGRATION = IntegrationManager.wrapper("ae2", () -> AE2Integration::new, EnderIO.modEventBus);
    public static final IntegrationWrapper<CCIntegration> CC_INTEGRATION = IntegrationManager.wrapper("computercraft", () -> CCIntegration::new, EnderIOBase.modEventBus);

    public static final IntegrationWrapper<ConduitSelfIntegration> SELF_INTEGRATION = IntegrationManager.wrapper("enderio", () -> ConduitSelfIntegration::new, EnderIOBase.modEventBus);

    public static void register() {
    }
}
