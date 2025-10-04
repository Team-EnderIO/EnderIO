package com.enderio.enderio.machines;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.machines.common.config.MachinesConfig;
import com.enderio.enderio.machines.common.config.MachinesConfigLang;
import com.enderio.enderio.machines.common.init.MachineAttachments;
import com.enderio.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.enderio.machines.common.init.MachineBlocks;
import com.enderio.enderio.machines.common.init.MachineDataComponents;
import com.enderio.enderio.machines.common.init.MachineMenus;
import com.enderio.enderio.machines.common.init.MachineRecipes;
import com.enderio.enderio.machines.common.init.MachineTravelTargets;
import com.enderio.enderio.machines.common.lang.MachineEnumLang;
import com.enderio.enderio.machines.common.lang.MachineLang;
import com.enderio.regilite.Regilite;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(EnderIO.MOD_ID)
public class EnderIOMachines {
    public static final Regilite REGILITE = EnderIO.REGILITE;

    public EnderIOMachines(IEventBus modEventBus, ModContainer modContainer) {
        // Register machine config
        modContainer.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");

        MachineDataComponents.register(modEventBus);
        MachineTravelTargets.register(modEventBus);
        MachineBlocks.register(modEventBus);
        MachineBlockEntities.register(modEventBus);
        MachineMenus.register(modEventBus);
        MachineRecipes.register(modEventBus);
        MachineAttachments.register(modEventBus);

        MachineLang.register();
        MachinesConfigLang.register();
        MachineEnumLang.register();
    }
}
