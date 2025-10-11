package com.enderio.enderio.compat.jade;

import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.client.content.conduits.model.facades.FacadeUtil;
import com.enderio.enderio.init.ConduitBlocks;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class EIOConduitsJadePlugin implements IWailaPlugin {

    // TODO: Could implement stuff like a waila tooltip for bound souls.

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Show the correct conduit (or facade item)
        registration.usePickedResult(ConduitBlocks.CONDUIT_BUNDLE.get());

        // Completely replace the block accessor with the facade block if it exists
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                if (blockAccessor.getBlockEntity() instanceof ConduitBundle conduitBundle && conduitBundle.hasFacade()
                        && FacadeUtil.areFacadesVisible(blockAccessor.getPlayer())) {
                    return registration.blockAccessor()
                            .from(blockAccessor)
                            .fakeBlock(conduitBundle.getFacadeBlock().asItem().getDefaultInstance())
                            .build();
                }
            }
            return accessor;
        });
    }
}
