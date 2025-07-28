package com.enderio.conduits.integration.ftb_ultimine;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.client.model.conduit.facades.FacadeUtil;
import com.enderio.conduits.client.particle.ConduitBreakParticle;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.google.common.collect.Maps;
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
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Map;

public enum ConduitBlockBreakHandler implements BlockBreakHandler {
    INSTANCE;

    private Map<Player, BreakOperation> breakOperations = Maps.newHashMap();

    // TODO: Not fully tested
    @Override
    public Result breakBlock(Player player, BlockPos pos, BlockState state, Shape shape, BlockHitResult hitResult) {
        // Note: Success means we've performed our actions (if any).
        //       Returning PASS means Ultimine will destroy the block for us.
        var level = player.level();
        var blockEntity = level.getBlockEntity(pos);
        if (!state.is(ConduitBlocks.CONDUIT) ||
            !(blockEntity instanceof ConduitBundleBlockEntity)) {
            return Result.PASS;
        }

        var conduitBundle = (ConduitBundleBlockEntity)blockEntity;

        // TODO: Find a way to share more logic with ConduitBundleBlock...
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
        if (operation == null) {
            return Result.FAIL;
        }

        // Attempt to apply operations
        if (operation instanceof FacadeBreakOperation facadeOperation) {
            if (!conduitBundle.hasFacade()) {
                return Result.SUCCESS;
            }

            if (!conduitBundle.getFacadeBlock().defaultBlockState().is(facadeOperation.facadeBlock)) {
                return Result.SUCCESS;
            }

            // Drop the facade item
            // TODO: Drop it at the origin pos...
            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    conduitBundle.dropFacadeItem();
                }
            }

            int lightLevelBefore = level.getLightEmission(pos);

            conduitBundle.setFacadeProvider(ItemStack.EMPTY);

            // Handle light update
            if (lightLevelBefore != level.getLightEmission(pos)) {
                level.getLightEngine().checkBlock(pos);
            }
        } else if (operation instanceof ConduitBreakOperation conduitOperation) {
            if (!conduitBundle.hasConduitStrict(conduitOperation.conduit)) {
                return Result.SUCCESS;
            }

            if (level.isClientSide) {
                ConduitBreakParticle.addDestroyEffects(pos, state, conduitOperation.conduit.value());
            }

            conduitBundle.removeConduit(conduitOperation.conduit, player);
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

    private static final class FacadeBreakOperation implements BreakOperation {
        private final Block facadeBlock;

        private FacadeBreakOperation(Block facadeBlock) {
            this.facadeBlock = facadeBlock;
        }
    }

    private static final class ConduitBreakOperation implements BreakOperation {
        private final Holder<Conduit<?, ?>> conduit;

        private ConduitBreakOperation(Holder<Conduit<?, ?>> conduit) {
            this.conduit = conduit;
        }
    }
}
