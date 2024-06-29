package com.enderio.conduits.common.integrations.jei;

import com.enderio.EnderIO;
import com.enderio.conduits.common.init.ConduitBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class ConduitsJEI implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return EnderIO.loc("conduits");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ConduitBlocks.CONDUIT.asItem(), new ConduitSubtypeInterpreter());
    }
}
