package com.enderio.enderio.compat.jade;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.client.content.conduits.model.facades.FacadeUtil;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class EIOJadePlugin implements IWailaPlugin {

    public static final ResourceLocation SOUL_BOUND_COMPONENT = EnderIO.rl("soul_bound");

    // TODO: Could implement stuff like a waila tooltip for bound souls.

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Use clone item stack to work out which item to show.
        registration.usePickedResult(EIOBlocks.CONDUIT_BUNDLE.get());

        // Completely replace the block accessor with the facade block if it exists.
        // This prevents any special widgets (such as exposed capabilities like energy) from appearing when covered by a facade.
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
