package com.enderio.enderio.conduits;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.enderio.conduits.common.init.ConduitComponents;
import com.enderio.enderio.conduits.common.init.ConduitIngredientTypes;
import com.enderio.enderio.conduits.common.init.ConduitItems;
import com.enderio.enderio.conduits.common.init.ConduitLang;
import com.enderio.enderio.conduits.common.init.ConduitMenus;
import com.enderio.enderio.conduits.common.init.ConduitTypes;
import com.enderio.regilite.Regilite;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(EnderIO.MOD_ID)
public class EnderIOConduits {
    public static final Regilite REGILITE = EnderIO.REGILITE;

    public EnderIOConduits(IEventBus modEventBus, ModContainer modContainer) {
        ConduitTypes.register(modEventBus);
        ConduitBlockEntities.register(modEventBus);
        ConduitMenus.register(modEventBus);
        ConduitBlocks.register(modEventBus);
        ConduitItems.register(modEventBus);
        ConduitComponents.register(modEventBus);
        ConduitIngredientTypes.register(modEventBus);
        ConduitLang.register();
    }
}
