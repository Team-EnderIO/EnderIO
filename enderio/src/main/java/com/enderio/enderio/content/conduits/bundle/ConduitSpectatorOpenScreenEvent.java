package com.enderio.enderio.content.conduits.bundle;

import com.enderio.enderio.content.conduits.menu.ConduitMenu;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Mod.EventBusSubscriber
public class ConduitSpectatorOpenScreenEvent {

    // Opening the conduit GUI allows spectators to open conduit gui while still
    // considering their selected conduit type.
    // we could probably have just opened any conduit to start but I think the
    // attention to detail is nice.
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseItem().isFalse() || !event.getEntity().isSpectator()) {
            return;
        }

        var level = event.getLevel();
        var hit = event.getHitVec();
        var pos = event.getPos();

        BlockState state = level.getBlockState(hit.getBlockPos());

        if (state.is(EIOBlocks.CONDUIT_BUNDLE)) {
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
