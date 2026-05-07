package com.enderio.enderio.compat.jade;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.client.content.conduits.model.facades.FacadeUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class EIOJadePlugin implements IWailaPlugin {

    public static final Identifier SOUL_BOUND_COMPONENT = EnderIO.id("soul_bound");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Completely replace the block accessor with the facade block if it exists.
        // This prevents any special widgets (such as exposed capabilities like energy) from appearing when covered by a facade.
        registration.addRayTraceCallback((_, accessor, _) -> {
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

        registration.registerBlockComponent(SoulBoundComponentProvider.INSTANCE, Block.class);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SoulBoundServerDataProvider.INSTANCE, Block.class);
    }
}
