package com.enderio.base.common.integration;


import java.util.function.Supplier;

public class IntegrationManager {

    //public static final IntegrationWrapper<DummyIntegration> DECORATION = wrapper("enderio_decoration", DummyIntegration::new);

    private static <T extends Integration> IntegrationWrapper<T> wrapper(String modid, Supplier<T> integration) {
        return new IntegrationWrapper<>(modid, integration);
    }
}
