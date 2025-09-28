package com.enderio.conduits.common.conduit.bundle;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.client.model.conduit.facades.FacadeUtil;
import com.enderio.conduits.client.particle.ConduitBreakParticle;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.network.C2SBreakConduitPacket;
import com.enderio.conduits.common.network.C2SRemoveConduitFacadePacket;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID)
public class ConduitBundleBlockBreakEventHandler {
    /**
     * Handles breaking the correct conduit within a bundle.
     * Implemented as an event to cancel block breaking before custom logic is applied.
     */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        var player = event.getEntity();
        var level = event.getLevel();
        var pos = event.getPos();

        var blockState = level.getBlockState(pos);
        if (!blockState.is(ConduitBlocks.CONDUIT)) {
            return;
        }

        // Cancel event for conduit, we control breaking from here on out.
        event.setCanceled(true);

        if (!(level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle)) {
            // TODO: Should we cancel after this check so the block can be broken?
            return;
        }

        // To ensure correctness, client will send custom packets to the server.
        if (!level.isClientSide()) {
            // If the bundle is empty, destroy it.
            // This is just in case of issues.
            if (conduitBundle.isEmpty()) {
                level.removeBlock(pos, false);
            }

            return;
        }

        // Remove facade, if visible
        if (conduitBundle.hasFacade() && FacadeUtil.areFacadesVisible(player)) {
            int lightLevelBefore = level.getLightEmission(pos);
            conduitBundle.setFacadeProvider(ItemStack.EMPTY);

            // Handle light update
            if (lightLevelBefore != level.getLightEmission(pos)) {
                level.getLightEngine().checkBlock(pos);
            }

            // Ask the server to remove the facade
            PacketDistributor.sendToServer(new C2SRemoveConduitFacadePacket(pos));
        } else {
            // Find the conduit to be removed.
            Holder<Conduit<?, ?>> conduit = null;
            if (conduitBundle.getConduits().size() == 1) {
                conduit = conduitBundle.getConduits().getFirst();
            } else if (conduitBundle.getConduits().size() > 1) {
                HitResult hit = player.pick(player.blockInteractionRange() + 5, 0.0f, false);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    conduit = conduitBundle.getShape().getConduit(((BlockHitResult) hit).getBlockPos(), hit);
                }
            }

//            if (true) {
//                // If the player is holding a conduit and this flag is enabled, they purposely want to break the held conduit.
//                conduit = ConduitA11yManager.getHeldConduit();
//
//                // If we don't have the held conduit, exit now.
//                if (conduit != null && !conduitBundle.hasConduitStrict(conduit)) {
//                    level.playSound(player, pos, SoundEvents.GENERIC_SMALL_FALL, SoundSource.BLOCKS, 1F, 1F);
//                    return false;
//                }
//
//                // TODO: If we adopt the strategy of only showing a bigger box when we're holding a conduit, we need to
//                // fire a packet to the server because we can't read whether the player is using the accessibility option on the server.
//
//                // TODO: It could also be possible to leave this in? Idk if this would accidentally fire if the client state is up to date...
//                }

            // Remove the conduit locally
            if (conduit != null) {
                // Remove the conduit
                ConduitBreakParticle.addDestroyEffects(pos, blockState, conduit.value());

                conduitBundle.removeConduit(conduit, droppedItem -> {});

                // Ask the server to remove the conduit
                PacketDistributor.sendToServer(new C2SBreakConduitPacket(pos, conduit));
            }
        }

        // If the bundle is empty, destroy the block.
        if (conduitBundle.isEmpty()) {
            level.setBlock(pos, level.getFluidState(pos).createLegacyBlock(), 11);
        }
    }
}
