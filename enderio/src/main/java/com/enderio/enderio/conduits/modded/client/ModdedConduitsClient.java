package com.enderio.enderio.conduits.modded.client;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.conduits.modded.client.modules.mekanism.MekanismClientModule;
import com.enderio.enderio.conduits.modded.common.ModuleModIds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

import java.util.Map;
import java.util.function.Supplier;

@Mod(value = EnderIOAPI.MOD_ID, dist = Dist.CLIENT)
public class ModdedConduitsClient {

    private static final Map<String, Supplier<ConduitClientModule>> CLIENT_MODULES = Map
            .ofEntries(Map.entry(ModuleModIds.MEKANISM, () -> MekanismClientModule.INSTANCE));

    public ModdedConduitsClient(IEventBus modEventBus) {
        for (var module : CLIENT_MODULES.entrySet()) {
            if (ModList.get().isLoaded(module.getKey())) {
                module.getValue().get().initialize(modEventBus);
            }
        }
    }
}
