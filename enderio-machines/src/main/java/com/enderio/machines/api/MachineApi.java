package com.enderio.machines.api;

import java.util.ServiceLoader;

public interface MachineApi {

    MachineApi INSTANCE = ServiceLoader.load(MachineApi.class).findFirst().orElseThrow();


}
