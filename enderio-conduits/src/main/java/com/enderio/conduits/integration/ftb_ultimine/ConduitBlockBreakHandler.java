package com.enderio.conduits.integration.ftb_ultimine;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.client.model.conduit.facades.FacadeUtil;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.ConduitBlocks;
import dev.ftb.mods.ftbultimine.api.blockbreaking.BlockBreakHandler;
import dev.ftb.mods.ftbultimine.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.concurrent.ConcurrentHashMap;

public enum ConduitBlockBreakHandler implements BlockBreakHandler {
    INSTANCE;

    // Should only ever happen from the main thread, but it cannot hurt to protect against.
    private final ConcurrentHashMap<Player, BreakOperation> breakOperations = new ConcurrentHashMap<>();

    @Override
    public Result breakBlock(Player player, BlockPos pos, BlockState state, Shape shape, BlockHitResult hitResult) {
        // Now that we have filtering in the selection handler, instead of returning SUCCESS for invalid bundles we should FAIL.

        // Note: Success means we've performed our actions (if any).
        //       Returning PASS means it's not our block.
        var level = player.level();
        var blockEntity = level.getBlockEntity(pos);
        if (!state.is(ConduitBlocks.CONDUIT) ||
            !(blockEntity instanceof ConduitBundleBlockEntity conduitBundle)) {
            return Result.PASS;
        }

        // TODO GH-1116: Find a way to share more logic with ConduitBundleBlock...
        BlockPos originPos = hitResult.getBlockPos();

        // Origin bundle breaking, capture necessary context.
        if (pos.equals(originPos)) {
            if (conduitBundle.hasFacade() && FacadeUtil.areFacadesVisible(player)) {
                breakOperations.put(player, new FacadeBreakOperation(conduitBundle.getFacadeBlock()));
            } else {
                Holder<Conduit<?, ?>> conduit = conduitBundle.getShape().getConduit(originPos, hitResult);
                if (conduit == null) {
                    return Result.FAIL;
                }

                breakOperations.put(player, new ConduitBreakOperation(conduit));
            }

            // Allow the origin bundle to make its own decisions
            return Result.PASS;
        }

        // Get operation
        BreakOperation operation = breakOperations.get(player);
        switch (operation) {
        case null -> {
            return Result.FAIL;
        }

        // Attempt to apply operations
        case FacadeBreakOperation(Block facadeBlock) -> {
            if (!conduitBundle.hasFacade()) {
                return Result.FAIL;
            }

            if (!conduitBundle.getFacadeBlock().defaultBlockState().is(facadeBlock)) {
                return Result.FAIL;
            }

            // Drop the facade item
            if (!player.getAbilities().instabuild) {
                conduitBundle.dropFacadeItem(originPos);
            }

            int lightLevelBefore = level.getLightEmission(pos);
            conduitBundle.setFacadeProvider(ItemStack.EMPTY);

            // Handle light update
            if (lightLevelBefore != level.getLightEmission(pos)) {
                level.getLightEngine().checkBlock(pos);
            }
        }
        case ConduitBreakOperation(Holder<Conduit<?, ?>> conduit) -> {
            if (!conduitBundle.hasConduitStrict(conduit)) {
                return Result.FAIL;
            }

            conduitBundle.removeConduit(conduit, player, originPos);
        }
        }

        if (conduitBundle.isEmpty()) {
            level.removeBlock(pos, false);
        } else {
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        }

        return Result.SUCCESS;
    }

    @Override
    public void postBreak(Player player) {
        breakOperations.remove(player);
    }

    private sealed interface BreakOperation {
    }

    private record FacadeBreakOperation(Block facadeBlock) implements BreakOperation {}
    private record ConduitBreakOperation(Holder<Conduit<?, ?>> conduit) implements BreakOperation {}
}
