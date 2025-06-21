package com.enderio.conduits.common.conduit.bundle;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.menu.ConduitMenu;
import com.enderio.conduits.common.init.ConduitBlocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID)
public class ConduitBlockEvent {

    // Opening the conduit GUI allows spectators to open conduit gui while still
    // considering their selected conduit type.
    // we could probably have just opened any conduit to start but I think the
    // attention to detail is nice.
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseItem().isFalse()) {
            return;
        }

        var level = event.getLevel();
        var hit = event.getHitVec();
        var pos = event.getPos();

        BlockState state = level.getBlockState(hit.getBlockPos());

        if (state.is(ConduitBlocks.CONDUIT)) {
            if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
                // TODO: The connection shouldn't include the plate.. if we hit the plate open
                // the first conduit?
                var conduitConnection = conduitBundle.getShape().getConnectionFromHit(pos, hit);

                if (conduitConnection != null) {
                    if (conduitBundle.canOpenScreen(conduitConnection.getSecond(), conduitConnection.getFirst())) {
                        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                            ConduitMenu.openConduitMenu(serverPlayer, conduitBundle, conduitConnection.getFirst(),
                                    conduitConnection.getSecond());
                        }

                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                    }
                }
            }
        }
    }
}
