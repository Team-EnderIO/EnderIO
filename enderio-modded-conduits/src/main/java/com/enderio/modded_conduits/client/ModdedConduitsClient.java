package com.enderio.modded_conduits.client;

import com.enderio.modded_conduits.client.modules.mekanism.MekanismClientModule;
import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.common.ModuleModIds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.function.Supplier;

@Mod(value = ModdedConduits.MOD_ID, dist = Dist.CLIENT)
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
