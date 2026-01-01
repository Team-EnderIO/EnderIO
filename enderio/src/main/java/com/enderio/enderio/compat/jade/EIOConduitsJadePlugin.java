package com.enderio.enderio.compat.jade;

import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.client.content.conduits.model.facades.FacadeUtil;
import com.enderio.enderio.init.EIOBlocks;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class EIOConduitsJadePlugin implements IWailaPlugin {

    // TODO: Could implement stuff like a waila tooltip for bound souls.

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Completely replace the block accessor with the facade block if it exists
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                if (blockAccessor.getBlockEntity() instanceof ConduitBundle conduitBundle && conduitBundle.hasFacade()
                        && FacadeUtil.areFacadesVisible(blockAccessor.getPlayer())) {
                    return registration.blockAccessor()
                            .from(blockAccessor)
                            .blockState(conduitBundle.getFacadeBlock().defaultBlockState())
                            .build();
                }
            }
            return accessor;
        });
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.blockOperations().pick(EIOBlocks.CONDUIT_BUNDLE.getKey());
    }
}
